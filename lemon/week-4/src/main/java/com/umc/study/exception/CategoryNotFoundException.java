package com.umc.study.exception;

// 존재하지 않는 categoryId로 도서를 등록하려 할 때 발생합니다.
public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long categoryId) {
        super("존재하지 않는 카테고리입니다. categoryId=" + categoryId);
    }
}
