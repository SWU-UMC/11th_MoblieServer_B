package com.umc.study.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "book")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long bookId;

    // 도서 여러 권은 하나의 카테고리에 속한다 (category 1 : N book)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    // 새 도서는 항상 대여 가능 상태로 등록 (3주차 INSERT의 is_available = true와 같은 규칙)
    public static Book create(Category category, String title, String description) {
        Book book = new Book();
        book.category = category;
        book.title = title;
        book.description = description;
        book.isAvailable = true;
        return book;
    }
}
