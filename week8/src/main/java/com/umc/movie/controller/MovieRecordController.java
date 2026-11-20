package com.umc.movie.controller;

import com.umc.movie.auth.LoginMember;
import com.umc.movie.config.SwaggerConfig;
import com.umc.movie.dto.ApiResponse;
import com.umc.movie.dto.CreateMovieRecordRequest;
import com.umc.movie.dto.MovieRecordResponse;
import com.umc.movie.service.MovieRecordService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "영화 기록", description = "[8주차] 평점 + 즐겨찾기를 하나의 트랜잭션으로 저장 (토큰 필요)")
@SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
@RestController
@RequiredArgsConstructor
public class MovieRecordController {

    private final MovieRecordService movieRecordService;

    // [8주차] 평점 + 즐겨찾기 한 번에 저장 (하나의 트랜잭션)
    @Operation(summary = "영화 기록 저장",
            description = "addToFavorites가 true면 평점과 즐겨찾기를 함께 저장하고, false면 평점만 저장합니다(favorite: null). "
                    + "둘 중 하나라도 실패하면 둘 다 저장되지 않습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "저장 성공",
                    content = @Content(examples = @ExampleObject(value = """
                            {"isSuccess": true, "code": "COMMON201", "message": "영화 기록을 저장했습니다.",
                             "result": {
                               "rating": {"ratingId": 3, "movieId": 238, "score": 5, "comment": "다시 보고 싶은 영화입니다.",
                                          "createdAt": "2026-11-20T15:10:00", "updatedAt": "2026-11-20T15:10:00"},
                               "favorite": {"favoriteId": 2, "movieId": 238, "createdAt": "2026-11-20T15:10:00"}}}"""))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 오류 (별점 범위, 필수값 누락)",
                    content = @Content(examples = @ExampleObject(value = """
                            {"isSuccess": false, "code": "COMMON400_1", "message": "별점은 5점 이하여야 합니다.", "result": null}"""))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰 없음 · 위조 · 만료",
                    content = @Content(examples = @ExampleObject(value = """
                            {"isSuccess": false, "code": "AUTH401_2", "message": "로그인이 필요한 요청입니다.", "result": null}"""))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 평점(RATING409_1) 또는 즐겨찾기(FAVORITE409_1)가 있음",
                    content = @Content(examples = {
                            @ExampleObject(name = "RATING409_1", value = """
                                    {"isSuccess": false, "code": "RATING409_1", "message": "이미 이 영화에 평점을 남기셨습니다.", "result": null}"""),
                            @ExampleObject(name = "FAVORITE409_1", value = """
                                    {"isSuccess": false, "code": "FAVORITE409_1", "message": "이미 이 영화를 즐겨찾기하셨습니다.", "result": null}""")
                    }))
    })
    @PostMapping("/movie-records")
    public ResponseEntity<ApiResponse<MovieRecordResponse>> create(
            @LoginMember Long memberId,
            @Valid @RequestBody CreateMovieRecordRequest request) {
        MovieRecordResponse response = movieRecordService.create(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("영화 기록을 저장했습니다.", response));
    }
}
