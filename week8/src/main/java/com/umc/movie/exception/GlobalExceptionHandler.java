package com.umc.movie.exception;

import com.umc.movie.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

// 어느 Controller에서 오류가 나도 { isSuccess: false, code, message, result: null } 형식으로 응답
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // [1번 문] 요청 DTO 검증(@Valid) 실패 → 400, 첫 번째 실패 이유를 message로
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return toResponse(ErrorCode.INVALID_INPUT, message);
    }

    // @PathVariable / @RequestParam 검증 실패 (5주차 닉네임 · 이메일 형식, page · size 범위)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodValidation(HandlerMethodValidationException e) {
        String message = e.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse(ErrorCode.INVALID_PARAMETER.getMessage());
        return toResponse(ErrorCode.INVALID_PARAMETER, message);
    }

    // /members/abc/ratings 처럼 숫자 자리에 문자가 온 경우
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return toResponse(ErrorCode.INVALID_PARAMETER, e.getName() + " 값의 형식이 올바르지 않습니다.");
    }

    // JSON 문법 오류, "score": "다섯" 같은 타입 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotReadable(HttpMessageNotReadableException e) {
        return toResponse(ErrorCode.INVALID_BODY, ErrorCode.INVALID_BODY.getMessage());
    }

    // [2번 문] 서비스 · 인증 검증 실패 (로그인 실패, 토큰 문제, 중복, 없는 데이터, 소유자 불일치)
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException e) {
        return ResponseEntity.status(e.getStatus())
                .body(ApiResponse.failure(e.getCode(), e.getMessage()));
    }

    // [3번 문] DB 제약 조건 위반
    // 사전 중복 체크를 동시에 통과한 요청은 INSERT 시점에 UNIQUE 제약에서 막힌다.
    // 어떤 제약이 위반됐는지 보고, 사전 체크와 같은 code로 바꿔서 화면이 구분 없이 처리할 수 있게 한다.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException e) {
        String cause = String.valueOf(e.getMostSpecificCause().getMessage());
        ErrorCode errorCode = resolveUniqueViolation(cause);
        log.warn("DB 제약 조건 위반 → {} ({})", errorCode.getCode(), cause);
        return toResponse(errorCode, errorCode.getMessage());
    }

    // 워크북 5주차 방식(ResponseStatusException)으로 던진 예외도 같은 형식으로
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatus(ResponseStatusException e) {
        int status = e.getStatusCode().value();
        return ResponseEntity.status(status).body(ApiResponse.failure("COMMON" + status, e.getReason()));
    }

    // [7주차] 6주차의 @ExceptionHandler(Exception.class) 만능 핸들러는 제거했다.
    // 없는 경로(404) · 허용하지 않는 메서드(405)까지 500 COMMON500으로 바꿔 버려서 원인 구분이 불가능했기 때문.
    // 대신 404 · 405는 상태 코드를 그대로 살려서 같은 응답 형식으로 돌려준다.
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NoResourceFoundException e) {
        return toResponse(ErrorCode.API_NOT_FOUND, ErrorCode.API_NOT_FOUND.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        return toResponse(ErrorCode.METHOD_NOT_ALLOWED, ErrorCode.METHOD_NOT_ALLOWED.getMessage());
    }

    // MySQL 메시지 예: Duplicate entry 'umc@example.com' for key 'member.email'
    private ErrorCode resolveUniqueViolation(String cause) {
        if (cause.contains("member.email")) {
            return ErrorCode.EMAIL_DUPLICATED;
        }
        if (cause.contains("member.nickname")) {
            return ErrorCode.NICKNAME_DUPLICATED;
        }
        if (cause.contains("uk_rating_member_movie")) {
            return ErrorCode.RATING_DUPLICATED;
        }
        // [8주차] 즐겨찾기 사전 검사를 동시에 통과한 요청
        if (cause.contains("uk_favorite_member_movie")) {
            return ErrorCode.FAVORITE_DUPLICATED;
        }
        return ErrorCode.DATA_CONFLICT;
    }

    private ResponseEntity<ApiResponse<Void>> toResponse(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.failure(errorCode.getCode(), message));
    }
}
