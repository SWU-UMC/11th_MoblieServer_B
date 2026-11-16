package com.umc.movie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// boolean 필드 이름이 is로 시작하면 JSON 키가 "favorite"로 바뀔 수 있어 이름을 고정합니다
public record FavoriteStatusResponse(
        @JsonProperty("isFavorite") boolean isFavorite
) {}
