package com.umc.movie.repository;

import com.umc.movie.entity.Rating;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    // [5주차] 유저별 평점 조회 (Slice: COUNT 쿼리 없이 페이지 단위 조회)
    @Query("select r from Rating r where r.member.memberId = :memberId order by r.ratingId desc")
    Slice<Rating> findSliceByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    // [6주차] 같은 회원이 같은 영화에 이미 평점을 남겼는지
    // 메서드 이름 방식(existsByMember_MemberIdAndMovieId)은 member를 LEFT JOIN했으므로,
    // 5주차와 같이 r.member.memberId로 비교해 rating.member_id(FK)만으로 확인
    @Query("select count(r) > 0 from Rating r where r.member.memberId = :memberId and r.movieId = :movieId")
    boolean existsByMember_MemberIdAndMovieId(@Param("memberId") Long memberId, @Param("movieId") Long movieId);
}
