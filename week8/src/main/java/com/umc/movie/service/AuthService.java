package com.umc.movie.service;

import com.umc.movie.dto.LoginRequest;
import com.umc.movie.dto.LoginResponse;
import com.umc.movie.entity.Member;
import com.umc.movie.exception.ApiException;
import com.umc.movie.exception.ErrorCode;
import com.umc.movie.repository.MemberRepository;
import com.umc.movie.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // [7주차] 로그인: 이메일 · 비밀번호 확인 후 Access Token 발급
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        // 없는 이메일과 틀린 비밀번호를 같은 code · message로 응답
        // → 어떤 이메일이 가입돼 있는지 응답으로 알아낼 수 없게 한다
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new ApiException(ErrorCode.LOGIN_FAILED));

        // 저장된 bcrypt 해시와 입력 평문을 비교 (해시를 다시 만들어 문자열 비교하면 salt 때문에 항상 다름)
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new ApiException(ErrorCode.LOGIN_FAILED);
        }

        String accessToken = jwtUtil.createAccessToken(member.getMemberId(), member.getEmail());
        return new LoginResponse(member.getNickname(), accessToken);
    }
}
