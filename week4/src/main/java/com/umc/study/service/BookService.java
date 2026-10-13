package com.umc.study.service;

import com.umc.study.domain.Book;
import com.umc.study.domain.Category;
import com.umc.study.dto.BookResponse;
import com.umc.study.dto.CreateBookRequest;
import com.umc.study.exception.CategoryNotFoundException;
import com.umc.study.repository.BookRepository;
import com.umc.study.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    // GET /books, GET /books?keyword=...
    public List<BookResponse> getBooks(String keyword) {
        List<Book> books = StringUtils.hasText(keyword)
                ? bookRepository.findByTitleContainingOrderByBookIdDesc(keyword.trim())
                : bookRepository.findAllByOrderByBookIdDesc();
        // Entity를 그대로 내보내지 않고 응답 DTO로 변환
        return books.stream().map(BookResponse::from).toList();
    }

    // POST /books
    @Transactional
    public BookResponse createBook(CreateBookRequest request) {
        // 없는 categoryId를 그대로 저장하지 않고 404로 응답
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));

        Book saved = bookRepository.save(Book.create(category, request.title(), request.description()));
        return BookResponse.from(saved);
    }
}
