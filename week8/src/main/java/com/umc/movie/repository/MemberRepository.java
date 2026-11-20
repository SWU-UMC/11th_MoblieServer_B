package com.umc.movie.repository;

import com.umc.movie.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    // [7주차] 로그인: 이메일로 회원 찾기
    Optional<Member> findByEmail(String email);

    // [7주차] 닉네임 수정: 나를 제외한 다른 회원이 이 닉네임을 쓰는지
    boolean existsByNicknameAndMemberIdNot(String nickname, Long memberId);
}
