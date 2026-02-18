# LoginController API 명세서

## 개요
이 컨트롤러는 사용자의 로그인 및 로그아웃 기능을 처리합니다.

## API 목록

### 1. 로그인 폼 페이지 조회

*   **설명:** 사용자에게 로그인 정보를 입력받을 수 있는 폼 페이지를 제공합니다.
*   **URI:** `/login`
*   **HTTP Method:** `GET`
*   **인증:** 필요 없음

#### 요청

*   **Body:** 없음

#### 응답

*   **성공 (HTTP 200 OK):**
    *   `login/loginForm.html` 뷰 페이지 반환

### 2. 로그인 처리

*   **설명:** 사용자가 제출한 아이디와 비밀번호를 검증하고, 유효한 경우 로그인 처리 후 세션에 사용자 정보를 저장합니다.
*   **URI:** `/login`
*   **HTTP Method:** `POST`
*   **인증:** 필요 없음 (로그인 과정)

#### 요청

*   **Query Parameters:**
    *   `redirectURL` (String, 선택): 로그인 성공 후 리다이렉트할 URL. 기본값은 `/`.
*   **Form Data (`application/x-www-form-urlencoded` 또는 `multipart/form-data`):**
    *   `loginId` (String, 필수): 사용자 로그인 아이디.
    *   `password` (String, 필수): 사용자 비밀번호.

#### 응답

*   **성공 (HTTP 302 Found):**
    *   지정된 `redirectURL`로 리다이렉트: `Location: {redirectURL}`
    *   `Set-Cookie` 헤더를 통해 `SESSION` 쿠키에 로그인 세션 ID 설정
*   **실패 (HTTP 200 OK):**
    *   `login/loginForm.html` 뷰 페이지 반환
    *   Model에 유효성 검증 실패 또는 "아이디 또는 비밀번호가 맞지 않습니다" 등의 에러 메시지 포함

### 3. 로그아웃 처리

*   **설명:** 현재 사용자의 세션을 무효화하여 로그아웃 처리하고 메인 페이지로 리다이렉트합니다.
*   **URI:** `/logout`
*   **HTTP Method:** `POST`
*   **인증:** 로그인된 사용자만 유효

#### 요청

*   **Header:**
    *   `Cookie`: `SESSION` (로그인 세션 ID)
*   **Body:** 없음

#### 응답

*   **성공 (HTTP 302 Found):**
    *   메인 페이지로 리다이렉트: `Location: /`
    *   `Set-Cookie` 헤더를 통해 `SESSION` 쿠키 무효화 (삭제)
