package com.umc.study.repository;

import com.umc.study.domain.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// SQL 문자열 없이, 메서드 이름만으로 Spring Data JPA가 쿼리를 만들어 줍니다.
public interface BookRepository extends JpaRepository<Book, Long> {

    // SELECT ... FROM book JOIN category ... ORDER BY book_id DESC
    // @EntityGraph로 category를 함께 조회해 N+1 쿼리를 막습니다.
    @EntityGraph(attributePaths = "category")
    List<Book> findAllByOrderByBookIdDesc();

    // SELECT ... FROM book WHERE category_id = ?
    @EntityGraph(attributePaths = "category")
    List<Book> findAllByCategory_CategoryId(Long categoryId);
}
