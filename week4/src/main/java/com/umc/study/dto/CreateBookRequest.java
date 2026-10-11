package com.umc.study.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

// POST /books 요청 본문의 약속 (3주차 Map<String, Object> 대체)
public record CreateBookRequest(
        @NotNull(message = "categoryId는 필수입니다.")
        @Positive(message = "categoryId는 1 이상이어야 합니다.")
        Long categoryId,

        @NotBlank(message = "title은 비어 있을 수 없습니다.")
        @Size(max = 100, message = "title은 100자 이하여야 합니다.")
        String title,

        String description // 선택 값
) {
}
