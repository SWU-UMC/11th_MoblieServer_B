-- 8주차 favorite 테이블
-- application.yaml이 ddl-auto: validate라서 Hibernate가 테이블과 제약을 만들지 않습니다.
-- 엔티티의 @UniqueConstraint는 설명일 뿐이므로 UNIQUE 제약을 여기서 직접 만듭니다.
-- 서비스의 중복 사전 검사를 동시 요청이 함께 통과해도 이 제약이 두 번째 INSERT를 막습니다.

CREATE TABLE IF NOT EXISTS favorite (
    favorite_id BIGINT      NOT NULL AUTO_INCREMENT,
    member_id   BIGINT      NOT NULL,
    movie_id    BIGINT      NOT NULL,
    created_at  DATETIME(6) NOT NULL,
    updated_at  DATETIME(6) NOT NULL,
    PRIMARY KEY (favorite_id),
    UNIQUE KEY uk_favorite_member_movie (member_id, movie_id),
    CONSTRAINT fk_favorite_member FOREIGN KEY (member_id) REFERENCES member (member_id)
);

-- 이미 favorite 테이블이 있는데 UNIQUE 제약이 없다면 아래를 실행합니다.
-- (중복된 (member_id, movie_id) 행이 있으면 실패하므로 먼저 중복을 지워야 합니다)
-- ALTER TABLE favorite ADD CONSTRAINT uk_favorite_member_movie UNIQUE (member_id, movie_id);
