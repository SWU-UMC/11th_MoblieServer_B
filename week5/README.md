# 5주차 미션 - 화면과 API를 처음 연결하기 (영화 평점 서비스)

- 스택: Spring Boot **3.5.0** (워크북 기준 고정), Java 21, Spring Data JPA, Validation, MySQL 8.0.43
- 새 프로젝트: `com.umc.movie`, 레이어 패키지(`controller` / `service` / `repository` / `entity` / `dto`) + `config`, `exception`
- DB: `umc_movie` ([sql/schema.sql](sql/schema.sql), [sql/seed.sql](sql/seed.sql)), `ddl-auto: validate`
- 화면: [frontend/index.html](frontend/index.html) — 브라우저 fetch로 API 3개를 모두 호출하는 웹 화면 (`http://localhost:5173`)

## 실행

```bash
# 1) DB
mysql -u root -p -e "CREATE DATABASE umc_movie DEFAULT CHARACTER SET utf8mb4"
mysql -u root -p umc_movie < sql/schema.sql
mysql -u root -p umc_movie < sql/seed.sql

# 2) 백엔드 (http://localhost:8080)
cp .env.example .env      # DB_PASSWORD 채우기 (IntelliJ 실행 구성의 환경 변수로 넣어도 됨)
./gradlew bootRun

# 3) 프론트 (http://localhost:5173)
cd frontend && npx serve -l 5173
```

## API 명세

| API | Method | URL | Request | Success Response (200) |
| --- | --- | --- | --- | --- |
| 닉네임 중복 체크 | GET | `/members/nickname/{nickname}` | Path: nickname (string, 2~12자) | `{ "available": true }` |
| 이메일 중복 체크 | GET | `/members/email/{email}` | Path: email (string, 이메일 형식) | `{ "available": true }` |
| 유저별 평점 조회 | GET | `/members/{memberId}/ratings` | Path: memberId (number)<br>Query: page (number, 기본 0), size (number, 기본 10, 최대 50) | `[{ "ratingId": 1, "movieId": 550, "score": 5, "comment": "최고의 영화!", "createdAt": "2026-08-26T10:00:00", "updatedAt": "2026-08-26T10:00:00" }]` (최신순) |

오류 응답: `{ "status": 400, "message": "닉네임은 2~12자여야 합니다." }`, `{ "status": 404, "message": "존재하지 않는 유저입니다." }`

## 실행 결과 ([api_results.txt](api_results.txt))

| 요청 | 결과 |
| --- | --- |
| `GET /members/nickname/수현` | 200 `{"available":false}` |
| `GET /members/nickname/제로` | 200 `{"available":true}` |
| `GET /members/email/noco@umc.com` | 200 `{"available":false}` |
| `GET /members/email/zero@umc.com` | 200 `{"available":true}` |
| `GET /members/1/ratings` | 200, ratingId 2 → 1 (최신순) |
| `GET /members/999/ratings` | 404 `존재하지 않는 유저입니다.` |
| `GET /members/abc/ratings` | 400 `memberId 값의 형식이 올바르지 않습니다.` |
| `GET /members/1/ratings?page=0&size=1` / `page=1` / `page=2` | 200, `[ratingId 2]` / `[ratingId 1]` / `[]` |
| `GET /members/1/ratings?page=-1&size=100` | 400 `page는 0 이상이어야 합니다. / size는 50 이하여야 합니다.` |
| `GET /members/nickname/a`, `abcdefghijklm` | 400 `닉네임은 2~12자여야 합니다.` |
| `GET /members/email/not-an-email` | 400 `이메일 형식이 올바르지 않습니다.` |

## CORS 확인 ([docs/screenshots](docs/screenshots))

| 단계 | 결과 |
| --- | --- |
| 설정 전, `localhost:5173` 화면에서 호출 | 콘솔: `blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present` — 하지만 서버 로그에는 SQL이 정상 실행됨 (요청은 도착, 브라우저가 응답을 차단) |
| `WebConfig` 설정 후, 5173에서 호출 | 3개 API 모두 화면에 정상 응답, 응답 헤더 `Access-Control-Allow-Origin: http://localhost:5173` |
| 같은 화면을 `localhost:5174`(다른 출처)로 띄워 호출 | 차단. 서버가 `403 Invalid CORS request`로 컨트롤러 실행 전에 거절 (SQL 0건) |
| `APP_CORS_ALLOWED_ORIGINS=*`로 실행 후 5174에서 호출 | 허용. `https://evil.example.com` 같은 임의 출처에도 `Access-Control-Allow-Origin: *` |

허용 출처는 `application.yml`의 `app.cors.allowed-origins`(기본 `http://localhost:5173`)로 관리하며, 코드 수정 없이 환경 변수 `APP_CORS_ALLOWED_ORIGINS`로 바꿀 수 있습니다.

## Postman

Postman "My Workspace"에 `UMC 5주차 - 영화 평점 API` 컬렉션(요청 12개, 상태 코드 테스트 포함)을 만들어 두었습니다. `baseUrl` 변수 = `http://localhost:8080`.
