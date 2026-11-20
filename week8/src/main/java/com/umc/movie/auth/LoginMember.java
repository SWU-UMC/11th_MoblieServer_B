package com.umc.movie.auth;

import io.swagger.v3.oas.annotations.Parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// [7주차 선택] 컨트롤러 파라미터에 붙이면 로그인한 회원의 memberId가 들어온다
// before: @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId
// after : @LoginMember Long memberId
// [8주차 선택] @Parameter(hidden = true): Swagger가 memberId를 요청 파라미터로 오해해 문서에 띄우지 않도록
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(hidden = true)
public @interface LoginMember {
}
