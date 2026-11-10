package com.umc.movie.exception;

import com.umc.movie.dto.ErrorResponse;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

// [선택] 형식 검증 실패를 { status, message } 형태의 400으로 응답
// (예외를 한 곳에서 다루는 본격적인 방법은 6주차에서 다룹니다)
@RestControllerAdvice
public class ValidationExceptionHandler {

    // @PathVariable / @RequestParam에 붙인 @Size, @Email, @Min, @Max 검증 실패
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleMethodValidation(HandlerMethodValidationException e) {
        String message = e.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(" / "));
        return ResponseEntity.badRequest().body(new ErrorResponse(400, message));
    }

    // /members/abc/ratings 처럼 숫자 자리에 문자가 온 경우
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(400, e.getName() + " 값의 형식이 올바르지 않습니다."));
    }

    // Service의 ResponseStatusException(404 "존재하지 않는 유저입니다.")도 같은 형식으로
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode())
                .body(new ErrorResponse(e.getStatusCode().value(), e.getReason()));
    }
}
