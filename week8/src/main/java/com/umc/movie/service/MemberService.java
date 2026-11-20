package com.umc.movie.service;

import com.umc.movie.dto.EmailCheckResponse;
import com.umc.movie.dto.MemberInfoResponse;
import com.umc.movie.dto.MemberResponse;
import com.umc.movie.dto.MemberUpdateRequest;
import com.umc.movie.dto.NicknameCheckResponse;
import com.umc.movie.dto.SignUpRequest;
import com.umc.movie.entity.Member;
import com.umc.movie.exception.ApiException;
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
            throw new ApiException(ErrorCode.EMAIL_DUPLICATED);
        }
        if (memberRepository.existsByNickname(request.nickname())) {
            throw new ApiException(ErrorCode.NICKNAME_DUPLICATED);
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

    // [7주차] 내 정보 조회 (memberId는 토큰에서 꺼낸 값)
    @Transactional(readOnly = true)
    public MemberInfoResponse getMyInfo(Long memberId) {
        return MemberInfoResponse.from(findMember(memberId));
    }

    // [7주차] 내 정보 수정 (닉네임)
    @Transactional
    public MemberInfoResponse updateMyInfo(Long memberId, MemberUpdateRequest request) {
        Member member = findMember(memberId);

        // 나 자신은 제외하고 중복 확인 → 지금 닉네임 그대로 보내도 409가 나지 않는다
        if (memberRepository.existsByNicknameAndMemberIdNot(request.nickname(), memberId)) {
            throw new ApiException(ErrorCode.NICKNAME_DUPLICATED);
        }

        member.updateNickname(request.nickname()); // 변경 감지로 UPDATE
        return MemberInfoResponse.from(member);
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
