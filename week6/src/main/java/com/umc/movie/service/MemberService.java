package com.umc.movie.service;

import com.umc.movie.dto.EmailCheckResponse;
import com.umc.movie.dto.MemberResponse;
import com.umc.movie.dto.NicknameCheckResponse;
import com.umc.movie.dto.SignUpRequest;
import com.umc.movie.entity.Member;
import com.umc.movie.exception.BusinessException;
import com.umc.movie.exception.ErrorCode;
import com.umc.movie.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // [5주차] 이미 존재하면 사용할 수 없으므로 available = !exists
    @Transactional(readOnly = true)
    public NicknameCheckResponse checkNickname(String nickname) {
        return new NicknameCheckResponse(!memberRepository.existsByNickname(nickname));
    }

    @Transactional(readOnly = true)
    public EmailCheckResponse checkEmail(String email) {
        return new EmailCheckResponse(!memberRepository.existsByEmail(email));
    }

    // [6주차] 회원가입
    @Transactional
    public MemberResponse signUp(SignUpRequest request) {
        // [2번 문] 서비스 검증: 이메일 · 닉네임 중복을 각각 다른 code로 알려준다
        if (memberRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATED);
        }
        if (memberRepository.existsByNickname(request.nickname())) {
            throw new BusinessException(ErrorCode.NICKNAME_DUPLICATED);
        }

        // 평문 비밀번호는 저장하지 않고 bcrypt 해시(60자)만 저장
        Member member = Member.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .build();

        // [3번 문] 위 검사를 동시에 통과한 요청은 여기서 DB UNIQUE 제약에 막힌다
        // → DataIntegrityViolationException → GlobalExceptionHandler가 같은 code로 변환
        return MemberResponse.from(memberRepository.save(member));
    }
}
