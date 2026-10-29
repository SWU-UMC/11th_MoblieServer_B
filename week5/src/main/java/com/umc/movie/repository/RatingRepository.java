package com.umc.movie.repository;

import com.umc.movie.entity.Rating;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    // [선택] 페이지네이션
    // - Slice: 전체 개수를 세는 COUNT 쿼리 없이 size + 1개만 조회 (응답에 전체 개수를 쓰지 않으므로)
    // - @Query: 메서드 이름 방식(findByMember_MemberId...)은 member를 LEFT JOIN했지만,
    //   r.member.memberId로 비교하면 rating.member_id(FK) 컬럼만으로 조회
    @Query("select r from Rating r where r.member.memberId = :memberId order by r.ratingId desc")
    Slice<Rating> findSliceByMemberId(@Param("memberId") Long memberId, Pageable pageable);
}
