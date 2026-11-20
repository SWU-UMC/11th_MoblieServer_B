package com.umc.movie.repository;

import com.umc.movie.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// RatingRepository와 같은 이유로 f.member.memberId로 비교해 favorite.member_id(FK)만으로 조회한다
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // 내 즐겨찾기 목록 (최신 등록 순)
    @Query("select f from Favorite f where f.member.memberId = :memberId order by f.favoriteId desc")
    List<Favorite> findByMember_MemberIdOrderByFavoriteIdDesc(@Param("memberId") Long memberId);

    @Query("select f from Favorite f where f.member.memberId = :memberId and f.movieId = :movieId")
    Optional<Favorite> findByMember_MemberIdAndMovieId(@Param("memberId") Long memberId, @Param("movieId") Long movieId);

    @Query("select count(f) > 0 from Favorite f where f.member.memberId = :memberId and f.movieId = :movieId")
    boolean existsByMember_MemberIdAndMovieId(@Param("memberId") Long memberId, @Param("movieId") Long movieId);
}
