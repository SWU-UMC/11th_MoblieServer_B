package com.umc.study.repository;

import com.umc.study.domain.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    // SELECT ... FROM book JOIN category ... ORDER BY book_id DESC
    // @EntityGraph: LAZY인 category를 이 조회에서만 JOIN으로 함께 가져와 N+1 쿼리 방지
    @EntityGraph(attributePaths = "category")
    List<Book> findAllByOrderByBookIdDesc();

    // [선택 심화] 제목 검색: WHERE title LIKE '%keyword%' ORDER BY book_id DESC
    @EntityGraph(attributePaths = "category")
    List<Book> findByTitleContainingOrderByBookIdDesc(String keyword);
}
