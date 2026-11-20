package com.umc.study.exception;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long categoryId) {
        super(categoryId + "번 카테고리가 존재하지 않습니다.");
    }
}
