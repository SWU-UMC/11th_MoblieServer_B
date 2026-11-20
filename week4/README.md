# 4주차 미션 - ORM으로 첫 API 리팩터링하기 (Spring Boot + JPA)

3주차 Raw SQL 도서 API([../week3](../week3))를 **Spring Data JPA + DTO + Bean Validation**으로 리팩터링했습니다. 3주차 코드는 그대로 두고 `feature/zero/week-4` 브랜치의 `week4/`에서 작업했습니다.

- 스택: Spring Boot 4.0.8, Java 21, Spring Data JPA(Hibernate), Bean Validation, MySQL 8.0.43
- DB: 2주차 `01_schema.sql`, `02_seed.sql`로 만든 `book_rental_w4`, `ddl-auto: validate` (기존 테이블 구조를 바꾸지 않고 엔티티와 일치하는지만 검사)

## 실행

```bash
cp .env.example .env   # DB_PASSWORD 채우기 (.env는 커밋 제외)
./gradlew bootRun
```

## 구조

```
src/main/java/com/umc/study
├── domain      Book, Category                  엔티티 (Book N : 1 Category, @ManyToOne LAZY)
├── dto         CreateBookRequest, BookResponse  요청 검증(@NotNull, @NotBlank, @Size) / 응답 형식
│               ErrorResponse                   모든 오류 응답의 공통 형식
├── repository  BookRepository, CategoryRepository (JpaRepository, @EntityGraph로 N+1 방지)
├── service     BookService                     카테고리 존재 확인, 엔티티 ↔ DTO 변환, @Transactional
├── controller  BookController                  @Valid, 201 Created
└── exception   GlobalExceptionHandler          400 / 404 / 500 응답 통일, CategoryNotFoundException
```

## API

| Method | URL | 설명 | 성공 |
| --- | --- | --- | --- |
| GET | `/books` | **[필수]** 전체 도서 최신순 (bookId, title, description, categoryName, isAvailable) | 200 |
| POST | `/books` | **[필수]** 도서 등록 `{ categoryId, title, description? }` | 201 |
| GET | `/books?keyword=코드` | **[선택 심화]** 제목 검색 (`findByTitleContaining`) | 200 |

카테고리 이름을 Response DTO로 반환하는 선택 심화 1번도 함께 구현했습니다.

## 검증 결과 ([api_results.txt](api_results.txt))

| 요청 | 결과 |
| --- | --- |
| `GET /books` | 200, 최신순 3권, `categoryName` 포함, DB 컬럼명 노출 없음 |
| `POST /books {"categoryId":1,"title":"클린 코드",...}` | 201, 저장된 도서를 DTO로 응답 |
| `POST /books {"categoryId":2,"title":"코스모스"}` | 201, description 생략 가능 |
| `POST /books {"categoryId":999,...}` | 404 `CATEGORY_NOT_FOUND` |
| `POST /books {"title":"   "}` | 400 `title은 비어 있을 수 없습니다.` |
| `POST /books {"title":"카테고리 누락"}` | 400 `categoryId는 필수입니다.` |
| title 101자 | 400 `title은 100자 이하여야 합니다.` |
| `{"categoryId":"abc"}` | 400 `INVALID_BODY` |
| `{"titel": ...}` (오타) | **400** `title은 비어 있을 수 없습니다.` — 3주차에서는 500이었음 |
| `GET /books?keyword=코드` | 200, `클린 코드` 1건 / 없는 키워드는 `[]` |

## N+1 확인

`@ManyToOne(fetch = LAZY)`만 두고 `GET /books`를 호출하면 `book` 조회 1번 + 카테고리 개수만큼 `category` 조회가 추가로 실행됐습니다(3권, 카테고리 2개 → 쿼리 3번). Repository 메서드에 `@EntityGraph(attributePaths = "category")`를 붙여 `book JOIN category` **쿼리 1번**으로 줄였습니다. (`logging.level.org.hibernate.SQL: debug`로 확인)

## 3주차 Raw SQL과 비교

- 3주차는 SQL 문자열과 `?` 파라미터 순서를 직접 맞췄지만, 4주차는 `findAllByOrderByBookIdDesc()`, `save()` 같은 Repository 메서드가 SQL을 만들어 줍니다.
- 3주차는 `Map<String, Object>`로 받아 `titel` 오타가 DB까지 가서 500이 났지만, 4주차는 `CreateBookRequest` + `@Valid`가 Service에 도달하기 전에 400으로 막고 어떤 필드가 왜 틀렸는지 알려 줍니다.
- 3주차는 `book_id`, `is_available` 같은 DB 컬럼명과 1/0 값이 그대로 응답에 나갔지만, 4주차는 `BookResponse`로 `bookId`, `isAvailable(true/false)`, `categoryName`만 골라 보냅니다.
- 3주차는 카테고리 존재 여부를 `SELECT COUNT(*)`로 직접 확인했지만, 4주차는 `categoryRepository.findById()`로 엔티티를 가져와 그대로 `Book`의 연관관계에 넣습니다.
- 대신 ORM은 연관 엔티티를 지연 로딩하면서 쿼리가 몰래 늘어날 수 있어(N+1), 실행되는 SQL을 확인하는 습관이 여전히 필요했습니다.
