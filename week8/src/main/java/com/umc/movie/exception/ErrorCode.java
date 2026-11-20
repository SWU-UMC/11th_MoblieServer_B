package com.umc.movie.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 화면이 code만 보고도 실패 이유를 구분할 수 있도록 오류를 한 곳에서 정의
// [7주차] code 규칙: {도메인}{HTTP 상태}_{번호}
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "COMMON400_1", "입력값이 올바르지 않습니다."),
    INVALID_BODY(HttpStatus.BAD_REQUEST, "COMMON400_2", "요청 본문을 읽을 수 없습니다. JSON 형식과 값의 타입을 확인해주세요."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "COMMON400_3", "경로 또는 쿼리 파라미터가 올바르지 않습니다."),
    API_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404_1", "존재하지 않는 API입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON405_1", "지원하지 않는 HTTP 메서드입니다."),
    DATA_CONFLICT(HttpStatus.CONFLICT, "COMMON409_1", "이미 존재하는 데이터입니다."),

    // 인증
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH401_1", "이메일 또는 비밀번호가 올바르지 않습니다."),
    TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "AUTH401_2", "로그인이 필요한 요청입니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH401_3", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH401_4", "만료된 토큰입니다. 다시 로그인해주세요."),

    // 회원
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404_1", "해당 회원을 찾을 수 없습니다."),
    NICKNAME_DUPLICATED(HttpStatus.CONFLICT, "MEMBER409_1", "이미 사용 중인 닉네임입니다."),
    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "MEMBER409_2", "이미 사용 중인 이메일입니다."),

    // 평점
    RATING_NOT_FOUND(HttpStatus.NOT_FOUND, "RATING404_1", "해당 평점을 찾을 수 없습니다."),
    RATING_FORBIDDEN(HttpStatus.FORBIDDEN, "RATING403_1", "본인이 작성한 평점만 수정하거나 삭제할 수 있습니다."),
    RATING_DUPLICATED(HttpStatus.CONFLICT, "RATING409_1", "이미 이 영화에 평점을 남기셨습니다."),

    // [8주차] 즐겨찾기
    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "FAVORITE404_1", "즐겨찾기한 영화를 찾을 수 없습니다."),
    FAVORITE_DUPLICATED(HttpStatus.CONFLICT, "FAVORITE409_1", "이미 이 영화를 즐겨찾기하셨습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
