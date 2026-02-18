# MemberController API 명세서

## 개요
이 컨트롤러는 회원 가입 관련 요청을 처리합니다.

## 기본 URI
`/members`

## API 목록

### 1. 회원 가입 폼 페이지 조회

*   **설명:** 신규 회원 등록을 위한 폼 페이지를 제공합니다.
*   **URI:** `/members/add`
*   **HTTP Method:** `GET`
*   **인증:** 필요 없음

#### 요청

*   **Body:** 없음

#### 응답

*   **성공 (HTTP 200 OK):**
    *   `members/addMemberForm.html` 뷰 페이지 반환

<br>


### 2. 신규 회원 등록 처리

*   **설명:** 사용자가 제출한 회원 정보를 바탕으로 신규 회원을 등록합니다. 아이디 중복 확인 및 유효성 검사를 수행합니다.
*   **URI:** `/members/add`
*   **HTTP Method:** `POST`
*   **인증:** 필요 없음

#### 요청

*   **Form Data (`application/x-www-form-urlencoded` 또는 `multipart/form-data`):**
    *   `loginId` (String, 필수): 사용자가 사용할 로그인 아이디.
    *   `password` (String, 필수): 사용자 비밀번호.
    *   `name` (String, 필수): 사용자 이름.

#### 응답

*   **성공 (HTTP 302 Found):**
    *   메인 페이지로 리다이렉트: `Location: /`
*   **실패 (HTTP 200 OK):**
    *   `members/addMemberForm.html` 뷰 페이지 반환
    *   Model에 유효성 검사 실패 또는 "아이디 중복" 등의 에러 메시지 포함
