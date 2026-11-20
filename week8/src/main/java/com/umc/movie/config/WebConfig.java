package com.umc.movie.config;

import com.umc.movie.auth.LoginMemberArgumentResolver;
import com.umc.movie.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final LoginMemberArgumentResolver loginMemberArgumentResolver;

    // 허용할 프론트엔드 출처 목록 (application.yml의 app.cors.allowed-origins, 기본값 http://localhost:5173)
    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해
                .allowedOrigins(allowedOrigins) // 이 출처에서 오는 요청만 허용
                .allowedMethods("GET", "POST", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    // [7주차] 기본은 모두 로그인 필요, 로그인 없이 쓰는 API만 제외
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/**",             // 로그인
                        "/members",             // 회원가입
                        "/members/nickname/**", // 닉네임 중복 확인
                        "/members/email/**",    // 이메일 중복 확인
                        "/members/*/ratings",   // 유저별 평점 조회 (공개)
                        "/swagger-ui/**",       // [8주차 선택] Swagger UI 화면
                        "/v3/api-docs/**",      // [8주차 선택] Swagger가 읽는 OpenAPI 문서
                        "/error"
                );
    }

    // [7주차 선택] @LoginMember 파라미터 처리기 등록
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgumentResolver);
    }
}
