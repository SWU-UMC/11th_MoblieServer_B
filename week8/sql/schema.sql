-- 5주차 워크북 제공 스키마 (영화 평점 서비스)
-- [8주차] created_at / updated_at을 DATETIME → DATETIME(6)으로 변경
-- DATETIME은 소수점 이하 초를 반올림해 저장해서, 저장 직후 응답(마이크로초)과 조회 응답(초 단위)의 시각이 달라졌기 때문
CREATE TABLE member (
    member_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    email       VARCHAR(254) NOT NULL UNIQUE,
    password    VARCHAR(60)  NOT NULL,
    nickname    VARCHAR(50)  NOT NULL UNIQUE,
    created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE rating (
    rating_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT   NOT NULL,
    movie_id    BIGINT   NOT NULL,
    score       TINYINT  NOT NULL,
    comment     TEXT,
    created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    FOREIGN KEY (member_id) REFERENCES member(member_id),
    UNIQUE KEY uk_rating_member_movie (member_id, movie_id)
);

-- 8주차 즐겨찾기 테이블 (5주차에 스키마만 만들어 둔 것을 이번 주차에 엔티티로 연결)
CREATE TABLE favorite (
    favorite_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT   NOT NULL,
    movie_id    BIGINT   NOT NULL,
    created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    FOREIGN KEY (member_id) REFERENCES member(member_id),
    UNIQUE KEY uk_favorite_member_movie (member_id, movie_id)
);
