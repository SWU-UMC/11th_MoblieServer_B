package com.umc.study.dto;

import java.util.List;

// 모든 오류 응답의 공통 형식
public record ErrorResponse(
        int status,
        String code,
        String message,
        List<FieldError> errors
) {
    public record FieldError(String field, String reason) {
    }

    public static ErrorResponse of(int status, String code, String message) {
        return new ErrorResponse(status, code, message, List.of());
    }
}
