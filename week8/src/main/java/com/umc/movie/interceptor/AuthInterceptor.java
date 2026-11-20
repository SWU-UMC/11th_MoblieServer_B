package com.umc.movie.interceptor;

import com.umc.movie.exception.ApiException;
import com.umc.movie.exception.ErrorCode;
import com.umc.movie.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// [7주차] 로그인이 필요한 요청 앞에서 토큰을 검사하는 문지기
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    public static final String MEMBER_ID = "memberId";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // CORS preflight(OPTIONS)는 토큰 없이 오므로 통과
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            throw new ApiException(ErrorCode.TOKEN_MISSING);
        }

        String token = header.substring(BEARER_PREFIX.length());
        // 검증 실패 시 JwtUtil이 AUTH401_3(위조) / AUTH401_4(만료)를 던진다
        request.setAttribute(MEMBER_ID, jwtUtil.getMemberId(token));
        return true;
    }
}
