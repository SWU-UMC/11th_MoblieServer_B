package com.umc.movie.controller;

import com.umc.movie.auth.LoginMember;
import com.umc.movie.config.SwaggerConfig;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.umc.movie.dto.ApiResponse;
import com.umc.movie.dto.CreateRatingRequest;
import com.umc.movie.dto.RatingResponse;
import com.umc.movie.dto.UpdateRatingRequest;
import com.umc.movie.service.RatingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "평점", description = "유저별 평점 조회는 공개, 등록 · 수정 · 삭제는 토큰 필요")
@RestController
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    // [5주차] 유저별 평점 조회 (최신순, ?page=0&size=10) — 로그인 없이 공개
    @GetMapping("/members/{memberId}/ratings")
    public List<RatingResponse> getMemberRatings(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "page는 0 이상이어야 합니다.") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "size는 1 이상이어야 합니다.")
            @Max(value = 50, message = "size는 50 이하여야 합니다.") int size) {
        return ratingService.getRatingsByMember(memberId, page, size);
    }

    // [7주차] 평점 등록 → 201
    // 6주차엔 /members/{memberId}/ratings 로 경로의 memberId를 믿었지만,
    // 이제 작성자는 토큰에서 꺼낸 memberId(@LoginMember)로 정한다
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @PostMapping("/ratings")
    public ResponseEntity<ApiResponse<RatingResponse>> create(
            @LoginMember Long memberId,
            @Valid @RequestBody CreateRatingRequest request) {
        RatingResponse response = ratingService.create(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("평점을 등록했습니다.", response));
    }

    // [7주차] 평점 수정 → 200 (본인 것이 아니면 403)
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @PatchMapping("/ratings/{ratingId}")
    public ResponseEntity<ApiResponse<RatingResponse>> update(
            @LoginMember Long memberId,
            @PathVariable Long ratingId,
            @Valid @RequestBody UpdateRatingRequest request) {
        RatingResponse response = ratingService.update(memberId, ratingId, request);
        return ResponseEntity.ok(ApiResponse.ok("평점을 수정했습니다.", response));
    }

    // [7주차] 평점 삭제 → 200 (본인 것이 아니면 403)
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @DeleteMapping("/ratings/{ratingId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @LoginMember Long memberId,
            @PathVariable Long ratingId) {
        ratingService.delete(memberId, ratingId);
        return ResponseEntity.ok(ApiResponse.ok("평점을 삭제했습니다.", null));
    }
}
