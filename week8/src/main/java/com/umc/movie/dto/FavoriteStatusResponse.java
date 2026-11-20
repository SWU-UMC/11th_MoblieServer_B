package com.umc.movie.dto;

// 즐겨찾기하지 않은 것도 정상 상태 → 200 + isFavorite: false
public record FavoriteStatusResponse(
        boolean isFavorite
) {
}
