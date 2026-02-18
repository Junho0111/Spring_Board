# MyPageController API 명세서

## 개요
이 컨트롤러는 로그인한 사용자의 마이페이지 관련 요청(마이페이지 홈, 내가 쓴 게시물 목록, 회원 정보 수정, 회원 탈퇴)을 처리합니다.

## 기본 URI
`/posts/my-page`

## API 목록

### 1. 마이페이지 홈 조회

*   **설명:** 로그인한 사용자의 마이페이지 홈을 조회합니다.
*   **URI:** `/posts/my-page`
*   **HTTP Method:** `GET`
*   **인증:** 필수 (로그인된 사용자)

#### 요청

*   **Header:**
    *   `Cookie`: `SESSION` (로그인 세션 ID)
*   **Body:** 없음

#### 응답

*   **성공 (HTTP 200 OK):**
    *   `mypage/myPageHome.html` 뷰 페이지 반환
    *   Model 포함: `loginMember` (로그인 회원 정보)

<br>


### 2. 내가 작성한 게시물 목록 조회

*   **설명:** 로그인한 사용자가 작성한 모든 게시물 목록을 조회합니다.
*   **URI:** `/posts/my-page/my-posts`
*   **HTTP Method:** `GET`
*   **인증:** 필수 (로그인된 사용자)

#### 요청

*   **Header:**
    *   `Cookie`: `SESSION` (로그인 세션 ID)
*   **Body:** 없음

#### 응답

*   **성공 (HTTP 200 OK):**
    *   `mypage/myPostsList.html` 뷰 페이지 반환
    *   Model 포함: `posts` (작성한 게시물 목록), `loginMember` (로그인 회원 정보)

<br>


### 3. 회원 정보 수정 폼 페이지 조회

*   **설명:** 로그인한 사용자의 회원 정보(이름, 비밀번호)를 수정하기 위한 폼 페이지를 제공합니다.
*   **URI:** `/posts/my-page/my-edit`
*   **HTTP Method:** `GET`
*   **인증:** 필수 (로그인된 사용자)

#### 요청

*   **Header:**
    *   `Cookie`: `SESSION` (로그인 세션 ID)
*   **Body:** 없음

#### 응답

*   **성공 (HTTP 200 OK):**
    *   `mypage/myEditForm.html` 뷰 페이지 반환
    *   Model 포함: `memberEditForm` (수정 폼 데이터 바인딩을 위한 빈 객체)

<br>


### 4. 회원 정보 수정 처리

*   **설명:** 로그인한 사용자가 제출한 정보로 이름과 비밀번호를 수정합니다.
*   **URI:** `/posts/my-page/my-edit`
*   **HTTP Method:** `POST`
*   **인증:** 필수 (로그인된 사용자)

#### 요청

*   **Header:**
    *   `Cookie`: `SESSION` (로그인 세션 ID)
*   **Form Data (`application/x-www-form-urlencoded` 또는 `multipart/form-data`):**
    *   `newName` (String, 필수): 변경할 사용자 이름.
    *   `newPassword` (String, 선택): 변경할 비밀번호 (입력하지 않으면 변경 안 됨).

#### 응답

*   **성공 (HTTP 302 Found):**
    *   마이페이지 홈으로 리다이렉트: `Location: /posts/my-page`
*   **실패 (HTTP 200 OK):**
    *   `mypage/myEditForm.html` 뷰 페이지 반환
    *   Model에 유효성 검사 실패 등의 에러 메시지 포함

<br>


### 5. 회원 탈퇴 처리

*   **설명:** 로그인한 사용자의 계정을 삭제하고 해당 세션을 무효화합니다.
*   **URI:** `/posts/my-page/delete`
*   **HTTP Method:** `POST`
*   **인증:** 필수 (로그인된 사용자)

#### 요청

*   **Header:**
    *   `Cookie`: `SESSION` (로그인 세션 ID)
*   **Body:** 없음

#### 응답

*   **성공 (HTTP 302 Found):**
    *   메인 페이지로 리다이렉트: `Location: /`
    *   `Set-Cookie` 헤더를 통해 `SESSION` 쿠키 무효화 (삭제)
