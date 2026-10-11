package com.umc.study.dto;

import com.umc.study.domain.Book;

// 응답에는 DB 컬럼명(book_id, is_available) 대신 API용 이름만 담는다
public record BookResponse(
        Long bookId,
        String title,
        String description,
        String categoryName,
        Boolean isAvailable
) {
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getBookId(),
                book.getTitle(),
                book.getDescription(),
                book.getCategory().getName(),
                book.getIsAvailable()
        );
    }
}
