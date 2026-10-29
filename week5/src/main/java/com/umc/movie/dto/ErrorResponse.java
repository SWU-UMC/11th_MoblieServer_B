package com.umc.movie.dto;

// [선택] 형식 검증 실패(400) 등 오류 응답 형식
public record ErrorResponse(int status, String message) {}
