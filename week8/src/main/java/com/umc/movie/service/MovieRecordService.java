package com.umc.movie.service;

import com.umc.movie.dto.CreateMovieRecordRequest;
import com.umc.movie.dto.FavoriteResponse;
import com.umc.movie.dto.MovieRecordResponse;
import com.umc.movie.dto.RatingResponse;
import com.umc.movie.entity.Favorite;
import com.umc.movie.entity.Member;
import com.umc.movie.entity.Rating;
import com.umc.movie.exception.ApiException;
import com.umc.movie.exception.ErrorCode;
import com.umc.movie.repository.FavoriteRepository;
import com.umc.movie.repository.MemberRepository;
import com.umc.movie.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [8주차] "영화 기록" = 평점 + (선택) 즐겨찾기를 한 번에 저장하는 사용자 행동
 *
 * RatingService.create()와 FavoriteService.create()를 연달아 부르지 않고,
 * 이 전용 Service의 메서드 하나에 @Transactional을 걸어 두 저장을 하나의 트랜잭션으로 묶는다.
 * - 메서드가 정상 종료 → commit (평점 + 즐겨찾기 모두 저장)
 * - 런타임 예외가 밖으로 나감 → rollback (먼저 INSERT한 평점도 취소)
 */
@Service
@RequiredArgsConstructor
public class MovieRecordService {

    private final MemberRepository memberRepository;
    private final RatingRepository ratingRepository;
    private final FavoriteRepository favoriteRepository;

    @Transactional
    public MovieRecordResponse create(Long memberId, CreateMovieRecordRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        // 사전 검사 (평점 중복은 7주차 코드 RATING409_1 그대로 사용)
        if (ratingRepository.existsByMember_MemberIdAndMovieId(memberId, request.movieId())) {
            throw new ApiException(ErrorCode.RATING_DUPLICATED);
        }
        if (request.addToFavorites()
                && favoriteRepository.existsByMember_MemberIdAndMovieId(memberId, request.movieId())) {
            throw new ApiException(ErrorCode.FAVORITE_DUPLICATED);
        }

        // 1) 평점 저장 (IDENTITY 전략이라 save() 시점에 INSERT가 바로 실행된다)
        Rating savedRating = ratingRepository.save(Rating.builder()
                .member(member)
                .movieId(request.movieId())
                .score(request.score())
                .comment(request.comment())
                .build());

        // 2) 즐겨찾기 저장: 여기서 실패하면 위의 평점 INSERT까지 rollback된다
        FavoriteResponse favoriteResponse = null;
        if (request.addToFavorites()) {
            Favorite savedFavorite = favoriteRepository.save(Favorite.builder()
                    .member(member)
                    .movieId(request.movieId())
                    .build());
            favoriteResponse = FavoriteResponse.from(savedFavorite);
        }

        return new MovieRecordResponse(RatingResponse.from(savedRating), favoriteResponse);
    }
}
