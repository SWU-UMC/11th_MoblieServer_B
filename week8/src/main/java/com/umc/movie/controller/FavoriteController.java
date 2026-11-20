package com.umc.movie.controller;

import com.umc.movie.auth.LoginMember;
import com.umc.movie.config.SwaggerConfig;
import com.umc.movie.dto.ApiResponse;
import com.umc.movie.dto.CreateFavoriteRequest;
import com.umc.movie.dto.FavoriteResponse;
import com.umc.movie.dto.FavoriteStatusResponse;
import com.umc.movie.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// [8주차] 즐겨찾기 API: 전부 로그인 필요 (인터셉터 기본값이 "모든 경로 보호"라 따로 설정할 것 없음)
// 7주차 선택 미션의 @LoginMember로 토큰의 memberId를 받는다 (요청 어디에도 memberId 없음)
@Tag(name = "즐겨찾기", description = "[8주차] 로그인한 회원의 즐겨찾기 (토큰 필요)")
@SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
@RestController
@RequiredArgsConstructor
@RequestMapping("/favorites")
public class FavoriteController {

    private static final String UNAUTHORIZED = """
            {"isSuccess": false, "code": "AUTH401_2", "message": "로그인이 필요한 요청입니다.", "result": null}""";

    private final FavoriteService favoriteService;

    @Operation(summary = "즐겨찾기 등록", description = "토큰의 회원이 movieId(TMDB 영화 ID)를 즐겨찾기합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "등록 성공",
                    content = @Content(examples = @ExampleObject(value = """
                            {"isSuccess": true, "code": "COMMON201", "message": "즐겨찾기를 등록했습니다.",
                             "result": {"favoriteId": 1, "movieId": 550, "createdAt": "2026-11-20T15:00:00"}}"""))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "movieId 누락 또는 0 이하",
                    content = @Content(examples = @ExampleObject(value = """
                            {"isSuccess": false, "code": "COMMON400_1", "message": "영화 ID는 양수여야 합니다.", "result": null}"""))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰 없음 · 위조 · 만료",
                    content = @Content(examples = @ExampleObject(value = UNAUTHORIZED))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 즐겨찾기한 영화",
                    content = @Content(examples = @ExampleObject(value = """
                            {"isSuccess": false, "code": "FAVORITE409_1", "message": "이미 이 영화를 즐겨찾기하셨습니다.", "result": null}""")))
    })
    @PostMapping
    public ResponseEntity<ApiResponse<FavoriteResponse>> create(
            @LoginMember Long memberId,
            @Valid @RequestBody CreateFavoriteRequest request) {
        FavoriteResponse response = favoriteService.create(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("즐겨찾기를 등록했습니다.", response));
    }

    @Operation(summary = "내 즐겨찾기 목록 조회", description = "최신 등록 순. 즐겨찾기가 없으면 빈 배열([])과 200입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(examples = @ExampleObject(value = """
                            {"isSuccess": true, "code": "COMMON200", "message": "내 즐겨찾기 목록을 조회했습니다.",
                             "result": [{"favoriteId": 2, "movieId": 238, "createdAt": "2026-11-20T15:10:00"},
                                        {"favoriteId": 1, "movieId": 550, "createdAt": "2026-11-20T15:00:00"}]}"""))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰 없음 · 위조 · 만료",
                    content = @Content(examples = @ExampleObject(value = UNAUTHORIZED)))
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<FavoriteResponse>>> getMyFavorites(@LoginMember Long memberId) {
        return ResponseEntity.ok(ApiResponse.ok("내 즐겨찾기 목록을 조회했습니다.", favoriteService.getMyFavorites(memberId)));
    }

    @Operation(summary = "특정 영화 즐겨찾기 여부 조회", description = "즐겨찾기하지 않았어도 오류가 아니라 200 + isFavorite: false입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(examples = @ExampleObject(value = """
                            {"isSuccess": true, "code": "COMMON200", "message": "즐겨찾기 여부를 조회했습니다.", "result": {"isFavorite": true}}"""))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰 없음 · 위조 · 만료",
                    content = @Content(examples = @ExampleObject(value = UNAUTHORIZED)))
    })
    @GetMapping("/{movieId}")
    public ResponseEntity<ApiResponse<FavoriteStatusResponse>> getStatus(
            @LoginMember Long memberId,
            @PathVariable Long movieId) {
        return ResponseEntity.ok(ApiResponse.ok("즐겨찾기 여부를 조회했습니다.", favoriteService.getStatus(memberId, movieId)));
    }

    // 삭제도 공통 응답 형식을 지키기 위해 204가 아닌 200 + result: null
    @Operation(summary = "즐겨찾기 해제", description = "내 즐겨찾기에서 해당 영화를 해제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "해제 성공",
                    content = @Content(examples = @ExampleObject(value = """
                            {"isSuccess": true, "code": "COMMON200", "message": "즐겨찾기를 해제했습니다.", "result": null}"""))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰 없음 · 위조 · 만료",
                    content = @Content(examples = @ExampleObject(value = UNAUTHORIZED))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "즐겨찾기하지 않은 영화",
                    content = @Content(examples = @ExampleObject(value = """
                            {"isSuccess": false, "code": "FAVORITE404_1", "message": "즐겨찾기한 영화를 찾을 수 없습니다.", "result": null}""")))
    })
    @DeleteMapping("/{movieId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @LoginMember Long memberId,
            @PathVariable Long movieId) {
        favoriteService.delete(memberId, movieId);
        return ResponseEntity.ok(ApiResponse.ok("즐겨찾기를 해제했습니다.", null));
    }
}
