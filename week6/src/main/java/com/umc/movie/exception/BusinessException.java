package com.umc.movie.exception;

import lombok.Getter;

// 서비스 검증에서 실패를 알릴 때 사용 (ErrorCode로 상태 코드 · code · message가 정해짐)
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
