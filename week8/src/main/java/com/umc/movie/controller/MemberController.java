package com.umc.movie.controller;

import com.umc.movie.auth.LoginMember;
import com.umc.movie.config.SwaggerConfig;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.umc.movie.dto.ApiResponse;
import com.umc.movie.dto.EmailCheckResponse;
import com.umc.movie.dto.MemberInfoResponse;
import com.umc.movie.dto.MemberResponse;
import com.umc.movie.dto.MemberUpdateRequest;
import com.umc.movie.dto.NicknameCheckResponse;
import com.umc.movie.dto.SignUpRequest;
import com.umc.movie.service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원", description = "회원가입 · 중복 확인은 공개, /members/me는 토큰 필요")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    // [5주차] 닉네임 중복 체크
    @GetMapping("/nickname/{nickname}")
    public NicknameCheckResponse checkNickname(
            @PathVariable @Size(min = 2, max = 12, message = "닉네임은 2~12자여야 합니다.") String nickname) {
        return memberService.checkNickname(nickname);
    }

    // [5주차] 이메일 중복 체크
    @GetMapping("/email/{email}")
    public EmailCheckResponse checkEmail(
            @PathVariable @Email(message = "이메일 형식이 올바르지 않습니다.") String email) {
        return memberService.checkEmail(email);
    }

    // [6주차] 회원가입: @Valid로 Service에 도달하기 전에 입력값 검증 → 성공 시 201
    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> signUp(@Valid @RequestBody SignUpRequest request) {
        MemberResponse response = memberService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("회원가입에 성공했습니다.", response));
    }

    // [7주차] 내 정보 조회 (로그인 필요)
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> getMyInfo(@LoginMember Long memberId) {
        return ResponseEntity.ok(ApiResponse.ok("회원 정보를 조회했습니다.", memberService.getMyInfo(memberId)));
    }

    // [7주차] 내 정보 수정 (로그인 필요)
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> updateMyInfo(
            @LoginMember Long memberId,
            @Valid @RequestBody MemberUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("회원 정보를 수정했습니다.", memberService.updateMyInfo(memberId, request)));
    }
}
