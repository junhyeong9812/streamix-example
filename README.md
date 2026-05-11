# Streamix Example

[![Docker](https://github.com/junhyeong9812/streamix-example/actions/workflows/docker-publish.yml/badge.svg)](https://github.com/junhyeong9812/streamix-example/actions/workflows/docker-publish.yml)
[![Docker Image](https://img.shields.io/badge/docker-ghcr.io%2Fjunhyeong9812%2Fstreamix--example-blue)](https://github.com/junhyeong9812/streamix-example/pkgs/container/streamix-example)

[Streamix](https://github.com/junhyeong9812/streamix) Spring Boot Starter의 **레퍼런스 컨슈머 앱**.
`@EnableStreamix` 한 줄로 활성화되는 미디어 스트리밍 서비스를 그대로 패키징해서 Docker 한 줄로 띄울 수 있게 한다.

---

## 🆕 v3.0.0 업데이트 (Streamix v3 대응)

소비자 API는 v2와 **완전 호환** — `@EnableStreamix`, `StreamixProperties`, REST 시그니처 동일. 변경된 것은 대시보드 UI 자산.

### 대시보드 재설계 (Cinema/Editorial Brutalist)
- **OKLCH 컬러 토큰** 기반 라이트/다크 듀얼 테마
- **시스템 폰트 스택** — 외부 폰트 다운로드 0 (`ui-serif`, `system-ui`, `ui-monospace`)
- 페이지 로드 staggered fade-in 애니메이션

### 신규 기능
- **다크/라이트/system 테마 토글** — localStorage 저장, OS 변경 자동 추적
- **자동 새로고침 정상화** — `GET /api/streamix/sessions/active` 5초 폴링
- **ES Module JS 아키텍처** — Event Bus / Store / Api 모듈 분리
- **공개 네임스페이스** — `window.Streamix.{events, store, api, theme, toast, modal, format}`

### 라이브러리 종속 0 방침
- WebJars Bootstrap / Bootstrap Icons / Locator Lite **모두 제거**
- Bootstrap Icons MIT SVG 25개를 self-host sprite로 차용
- 외부 폰트(Google Fonts 등) 사용 안 함
- npm/Node 빌드 단계 없음 — `./gradlew build` 한 줄로 끝

---

## 🚀 Quick Start

### Docker로 실행 (H2 인메모리)

```bash
docker pull ghcr.io/junhyeong9812/streamix-example:latest

docker run -d \
  -p 8080:8080 \
  -v ./data:/app/streamix-data \
  --name streamix-example \
  ghcr.io/junhyeong9812/streamix-example:latest
```

### 파일 타입 제한하여 실행

```bash
# 이미지와 비디오만 허용, 50MB 상한
docker run -d \
  -p 8080:8080 \
  -e STREAMIX_STORAGE_ALLOWED_TYPES=IMAGE,VIDEO \
  -e STREAMIX_STORAGE_MAX_FILE_SIZE=52428800 \
  -v ./data:/app/streamix-data \
  --name streamix-example \
  ghcr.io/junhyeong9812/streamix-example:latest
```

### 접속

- **대시보드**: http://localhost:8080/streamix
- **API**: http://localhost:8080/api/streamix/files
- **H2 Console**: http://localhost:8080/h2-console

---

## 📁 지원 파일 타입

| 타입 | 설명 | 예시 확장자 | 대시보드 미리보기 |
|------|------|------------|------------------|
| `IMAGE` | 이미지 파일 | jpg, png, gif, webp | 이미지 뷰어 |
| `VIDEO` | 비디오 파일 | mp4, webm, avi, mkv | 비디오 플레이어 |
| `AUDIO` | 오디오 파일 | mp3, wav, flac, aac | 오디오 플레이어 |
| `DOCUMENT` | 문서 파일 | pdf, doc, xlsx, txt | 다운로드 링크 |
| `ARCHIVE` | 압축 파일 | zip, rar, 7z, tar.gz | 다운로드 링크 |
| `OTHER` | 기타 파일 | 그 외 모든 파일 | 다운로드 링크 |

---

## 🔌 API Endpoints

### 파일 관리 API

| Method | Endpoint | 설명 | Request | Response |
|--------|----------|------|---------|----------|
| `POST` | `/api/streamix/files` | 파일 업로드 | `multipart/form-data` | `FileResponse` |
| `GET` | `/api/streamix/files` | 파일 목록 | `?page=0&size=20` | `Page<FileResponse>` |
| `GET` | `/api/streamix/files/{id}` | 파일 정보 조회 | - | `FileResponse` |
| `DELETE` | `/api/streamix/files/{id}` | 파일 삭제 | - | `204 No Content` |

### 스트리밍 API

| Method | Endpoint | 설명 | Headers |
|--------|----------|------|---------|
| `GET` | `/api/streamix/files/{id}/stream` | 파일 스트리밍 | `Range: bytes=0-1023` |
| `GET` | `/api/streamix/files/{id}/thumbnail` | 썸네일 조회 | - |
| `GET` | `/api/streamix/sessions/active` | **(v3 신규)** 활성 세션 (5초 폴링용) | - |

### 대시보드 페이지

| Endpoint | 설명 |
|----------|------|
| `/streamix` | 메인 대시보드 (통계, 최근 업로드) |
| `/streamix/files` | 파일 목록 (그리드/리스트 뷰) |
| `/streamix/files/{id}` | 파일 상세/미리보기 |
| `/streamix/sessions` | 스트리밍 세션 모니터 |

### API 사용 예시

```bash
# 파일 업로드
curl -X POST http://localhost:8080/api/streamix/files \
  -F "file=@video.mp4"

# 파일 목록 조회
curl http://localhost:8080/api/streamix/files

# 파일 스트리밍 (Range Request)
curl -H "Range: bytes=0-1023" \
  http://localhost:8080/api/streamix/files/{id}/stream

# 파일 삭제
curl -X DELETE http://localhost:8080/api/streamix/files/{id}
```

### 에러 응답

**파일 크기 초과 (413):**
```json
{
  "timestamp": "2026-05-12T10:30:00",
  "status": 413,
  "error": "Payload Too Large",
  "code": "FILE_SIZE_EXCEEDED",
  "message": "File size 150MB exceeds maximum allowed size 100MB"
}
```

**허용되지 않는 파일 타입 (400):**
```json
{
  "timestamp": "2026-05-12T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "code": "INVALID_FILE_TYPE",
  "message": "File type ARCHIVE is not allowed. Allowed types: [IMAGE, VIDEO]"
}
```

---

## 🗄️ 데이터베이스 설정

기본값은 **H2 인메모리**이며, 환경변수로 PostgreSQL, MySQL 등으로 변경 가능.

### H2 (기본값 - 테스트/개발용)

```bash
docker run -d -p 8080:8080 \
  ghcr.io/junhyeong9812/streamix-example:latest
```

### PostgreSQL

```bash
docker run -d -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/filedb \
  -e SPRING_DATASOURCE_DRIVER=org.postgresql.Driver \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  -e SPRING_JPA_DDL_AUTO=update \
  -v ./data:/app/streamix-data \
  ghcr.io/junhyeong9812/streamix-example:latest
```

### MySQL

```bash
docker run -d -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/filedb \
  -e SPRING_DATASOURCE_DRIVER=com.mysql.cj.jdbc.Driver \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  -e SPRING_JPA_DDL_AUTO=update \
  -v ./data:/app/streamix-data \
  ghcr.io/junhyeong9812/streamix-example:latest
```

---

## 📦 Docker Compose

### 기본 (H2)

```yaml
services:
  streamix:
    image: ghcr.io/junhyeong9812/streamix-example:latest
    ports:
      - "8080:8080"
    volumes:
      - ./data:/app/streamix-data
```

### PostgreSQL 연동 + 파일 타입 제한

```yaml
services:
  streamix:
    image: ghcr.io/junhyeong9812/streamix-example:latest
    ports:
      - "8080:8080"
    environment:
      # Database
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/filedb
      - SPRING_DATASOURCE_DRIVER=org.postgresql.Driver
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=secret
      - SPRING_JPA_DDL_AUTO=update
      - H2_CONSOLE_ENABLED=false
      # Streamix Storage
      - STREAMIX_STORAGE_BASE_PATH=/app/streamix-data
      - STREAMIX_STORAGE_MAX_FILE_SIZE=104857600
      - STREAMIX_STORAGE_ALLOWED_TYPES=IMAGE,VIDEO,AUDIO
    volumes:
      - file_data:/app/streamix-data
    depends_on:
      - postgres

  postgres:
    image: postgres:16-alpine
    environment:
      - POSTGRES_DB=filedb
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=secret
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  file_data:
  postgres_data:
```

---

## ⚙️ 환경 변수

### 데이터베이스

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `SPRING_DATASOURCE_URL` | `jdbc:h2:mem:streamix` | JDBC URL |
| `SPRING_DATASOURCE_DRIVER` | `org.h2.Driver` | JDBC 드라이버 |
| `SPRING_DATASOURCE_USERNAME` | `sa` | DB 사용자명 |
| `SPRING_DATASOURCE_PASSWORD` | *(empty)* | DB 비밀번호 |
| `SPRING_JPA_DDL_AUTO` | `create-drop` | DDL 전략 |
| `H2_CONSOLE_ENABLED` | `true` | H2 콘솔 활성화 |

### Streamix Storage

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `STREAMIX_STORAGE_BASE_PATH` | `./streamix-data` | 파일 저장 경로 |
| `STREAMIX_STORAGE_MAX_FILE_SIZE` | `104857600` | 최대 파일 크기 (바이트) |
| `STREAMIX_STORAGE_ALLOWED_TYPES` | *(empty)* | 허용 파일 타입 (빈 값 = 전체 허용) |

### Streamix Thumbnail

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `STREAMIX_THUMBNAIL_ENABLED` | `true` | 썸네일 생성 활성화 |
| `STREAMIX_THUMBNAIL_WIDTH` | `320` | 썸네일 너비 |
| `STREAMIX_THUMBNAIL_HEIGHT` | `180` | 썸네일 높이 |
| `STREAMIX_THUMBNAIL_FFMPEG_PATH` | `ffmpeg` | FFmpeg 경로 |

### Streamix API & Dashboard

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `STREAMIX_API_ENABLED` | `true` | REST API 활성화 |
| `STREAMIX_API_BASE_PATH` | `/api/streamix` | API 기본 경로 |
| `STREAMIX_DASHBOARD_ENABLED` | `true` | 대시보드 활성화 |
| `STREAMIX_DASHBOARD_PATH` | `/streamix` | 대시보드 경로 |

### 서버

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `SERVER_PORT` | `8080` | 서버 포트 |
| `MAX_FILE_SIZE` | `100MB` | 업로드 최대 크기 |
| `MAX_REQUEST_SIZE` | `100MB` | 요청 최대 크기 |

---

## 🎨 테마 토글 (v3 신규)

대시보드는 기본적으로 OS 설정(`prefers-color-scheme`)을 따른다. 강제로 고정하려면:

```javascript
// 라이트 모드 강제
localStorage.setItem('streamix.theme', 'light');

// 다크 모드 강제
localStorage.setItem('streamix.theme', 'dark');

// OS 설정 따르기 (기본값)
localStorage.removeItem('streamix.theme');
```

대시보드 우상단 토글 버튼으로도 동일하게 변경 가능.

---

## 🏗️ 직접 빌드

### 요구사항
- Java 25+
- Gradle 8.x+

### 환경 변수 설정

```bash
cp .env.example .env
# .env 파일 편집
```

### 빌드 & 실행

```bash
git clone https://github.com/junhyeong9812/streamix-example.git
cd streamix-example

./gradlew build
./gradlew bootRun
```

### Docker 이미지 빌드

```bash
docker build -t streamix-example .
docker run -p 8080:8080 streamix-example
```

---

## 📚 기술 스택

- **Java 25**
- **Spring Boot 4.0**
- **Streamix 3.0.0** — Spring Boot Starter 파일 스트리밍 라이브러리
- **H2 / PostgreSQL / MySQL** — 데이터베이스 (선택)
- **Thymeleaf** — 대시보드 UI
- **FFmpeg** — 비디오 썸네일 생성

---

## 🔗 관련 프로젝트

- [Streamix](https://github.com/junhyeong9812/streamix) — 본체 Spring Boot Starter 라이브러리

---

## 📄 License

MIT License

---

<p align="center">
  Made by <a href="https://github.com/junhyeong9812">junhyeong9812</a>
</p>
