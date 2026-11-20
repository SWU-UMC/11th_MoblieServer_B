package com.umc.movie.util;

import com.umc.movie.exception.ApiException;
import com.umc.movie.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// [7주차] Access Token 발급 · 검증
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessExpirationMillis;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.access-expiration}") long accessExpirationSeconds) {
        // 비밀 문자열 → HMAC 키 (32바이트 미만이면 WeakKeyException으로 서버가 뜨지 않음)
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationMillis = accessExpirationSeconds * 1000;
    }

    // 로그인 성공 시 발급: sub = memberId, email 클레임, 발급/만료 시각
    public String createAccessToken(Long memberId, String email) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim("email", email)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessExpirationMillis))
                .signWith(secretKey)
                .compact();
    }

    // 서명 · 만료를 검증하고 memberId를 꺼낸다
    public Long getMemberId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .clockSkewSeconds(60) // 서버 간 시계 오차 허용 (만료 후 최대 60초까지 통과)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Long.valueOf(claims.getSubject());
        } catch (ExpiredJwtException e) {
            throw new ApiException(ErrorCode.TOKEN_EXPIRED);   // 만료
        } catch (JwtException | IllegalArgumentException e) {
            throw new ApiException(ErrorCode.TOKEN_INVALID);   // 위조 · 형식 오류
        }
    }
}
