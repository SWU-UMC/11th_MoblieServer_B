package com.umc.movie.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// [7주차] 실패 상황을 HTTP 상태 · code · message와 함께 던지는 예외
// GlobalExceptionHandler가 받아서 { isSuccess: false, code, message, result: null } 로 응답한다
@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public ApiException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    // 자주 쓰는 실패는 ErrorCode에 모아 두고 꺼내 쓴다
    public ApiException(ErrorCode errorCode) {
        this(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage());
    }
}
