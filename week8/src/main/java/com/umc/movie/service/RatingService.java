package com.umc.movie.service;

import com.umc.movie.dto.CreateRatingRequest;
import com.umc.movie.dto.RatingResponse;
import com.umc.movie.dto.UpdateRatingRequest;
import com.umc.movie.entity.Member;
import com.umc.movie.entity.Rating;
import com.umc.movie.exception.ApiException;
import com.umc.movie.exception.ErrorCode;
import com.umc.movie.repository.MemberRepository;
import com.umc.movie.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final MemberRepository memberRepository;

    // [5주차] 유저별 평점 조회 (최신순, page · size)
    @Transactional(readOnly = true)
    public List<RatingResponse> getRatingsByMember(Long memberId, int page, int size) {
        if (!memberRepository.existsById(memberId)) {
            throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);
        }
        return ratingRepository.findSliceByMemberId(memberId, PageRequest.of(page, size))
                .stream()
                .map(RatingResponse::from)
                .toList();
    }

    // [6주차] 평점 등록 → [7주차] memberId는 토큰에서, 응답은 등록된 평점
    @Transactional
    public RatingResponse create(Long memberId, CreateRatingRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        // rating 테이블의 (member_id, movie_id) 복합 UNIQUE가 최종적으로 막아주지만,
        // 정해 둔 code · message로 응답하기 위해 먼저 확인한다
        if (ratingRepository.existsByMember_MemberIdAndMovieId(memberId, request.movieId())) {
            throw new ApiException(ErrorCode.RATING_DUPLICATED);
        }

        Rating rating = Rating.builder()
                .member(member)
                .movieId(request.movieId())
                .score(request.score())
                .comment(request.comment())
                .build();

        return RatingResponse.from(ratingRepository.save(rating));
    }

    // [6주차] 평점 수정 → [7주차] 수정된 평점을 응답
    @Transactional
    public RatingResponse update(Long memberId, Long ratingId, UpdateRatingRequest request) {
        Rating rating = findOwnedRating(memberId, ratingId);
        rating.update(request.score(), request.comment()); // 변경 감지로 UPDATE
        // @UpdateTimestamp는 flush(UPDATE 실행) 시점에 채워진다.
        // flush 전에 응답을 만들면 updatedAt이 수정 전 값으로 나가므로 먼저 UPDATE를 내보낸다
        ratingRepository.flush();
        return RatingResponse.from(rating);
    }

    // [6주차] 평점 삭제
    @Transactional
    public void delete(Long memberId, Long ratingId) {
        Rating rating = findOwnedRating(memberId, ratingId);
        ratingRepository.delete(rating);
    }

    /**
     * 평점을 찾고, 그 평점이 요청자의 것인지까지 확인한다. (수정 · 삭제 공통)
     * 먼저 찾아보고(없으면 404), 찾았으면 주인을 본다(내 것이 아니면 403).
     */
    private Rating findOwnedRating(Long memberId, Long ratingId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ApiException(ErrorCode.RATING_NOT_FOUND));

        if (!rating.isWrittenBy(memberId)) {
            throw new ApiException(ErrorCode.RATING_FORBIDDEN);
        }
        return rating;
    }
}
