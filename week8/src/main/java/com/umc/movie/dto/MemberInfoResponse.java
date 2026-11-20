package com.umc.movie.dto;

import com.umc.movie.entity.Member;

import java.time.LocalDateTime;

// 내 정보 조회 · 수정 응답 (password는 담지 않는다)
public record MemberInfoResponse(String email, String nickname, LocalDateTime createdAt) {

    public static MemberInfoResponse from(Member member) {
        return new MemberInfoResponse(member.getEmail(), member.getNickname(), member.getCreatedAt());
    }
}
