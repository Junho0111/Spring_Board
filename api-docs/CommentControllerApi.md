# CommentController API 명세서

## 개요
이 컨트롤러는 게시물에 대한 댓글 및 답글의 추가, 수정, 삭제 요청을 처리합니다.

## 기본 URI
`/posts/{postId}/comments`

## API 목록

### 1. 댓글 또는 답글 추가

*   **설명:** 특정 게시물에 새 댓글을 추가하거나 기존 댓글에 답글을 추가합니다.
*   **URI:** `/posts/{postId}/comments`
*   **HTTP Method:** `POST`
*   **인증:** 필수 (로그인된 사용자)

#### 요청

*   **Path Variables:**
    *   `postId` (Long): 댓글을 추가할 게시물의 고유 ID.
*   **Form Data (`application/x-www-form-urlencoded` 또는 `multipart/form-data`):**
    *   `content` (String, 필수): 댓글 내용.
    *   `parentCommentId` (Long, 선택): 답글인 경우 부모 댓글의 ID.
*   **Header:**
    *   `Cookie`: `SESSION` (로그인 세션 ID)

#### 응답

*   **성공 (HTTP 302 Found):**
    *   게시물 상세 페이지로 리다이렉트: `Location: /posts/{postId}`
    *   `successMessage` 플래시 속성 포함
*   **실패 (HTTP 302 Found):**
    *   게시물이 존재하지 않거나, 부모 댓글이 유효하지 않거나, 유효성 검증 실패 시 게시물 상세 페이지 또는 메인 페이지로 리다이렉트.
    *   `errorMessage` 플래시 속성 포함

<br>

### 2. 댓글 수정 폼 제공

*   **설명:** 특정 댓글을 수정하기 위한 폼 페이지를 제공합니다.
*   **URI:** `/posts/{postId}/comments/{commentId}/edit`
*   **HTTP Method:** `GET`
*   **인증:** 필수 (로그인된 사용자 중 해당 댓글의 작성자)

#### 요청

*   **Path Variables:**
    *   `postId` (Long): 게시물 ID.
    *   `commentId` (Long): 수정할 댓글의 고유 ID.
*   **Header:**
    *   `Cookie`: `SESSION` (로그인 세션 ID)

#### 응답

*   **성공 (HTTP 200 OK):**
    *   `comments/editForm.html` 뷰 페이지 반환
    *   Model 포함: `commentForm` (수정할 댓글 내용), `postId`, `commentId`
*   **실패 (HTTP 302 Found):**
    *   댓글이 없거나, 게시물 ID와 댓글 ID 불일치, 수정 권한 없음 시 게시물 상세 페이지로 리다이렉트.
    *   `errorMessage` 플래시 속성 포함

<br>


### 3. 댓글 수정 처리

*   **설명:** 특정 댓글의 내용을 수정합니다.
*   **URI:** `/posts/{postId}/comments/{commentId}/edit`
*   **HTTP Method:** `POST`
*   **인증:** 필수 (로그인된 사용자 중 해당 댓글의 작성자)

#### 요청

*   **Path Variables:**
    *   `postId` (Long): 게시물 ID.
    *   `commentId` (Long): 수정할 댓글의 고유 ID.
*   **Form Data (`application/x-www-form-urlencoded` 또는 `multipart/form-data`):**
    *   `content` (String, 필수): 수정할 댓글 내용.
*   **Header:**
    *   `Cookie`: `SESSION` (로그인 세션 ID)

#### 응답

*   **성공 (HTTP 302 Found):**
    *   게시물 상세 페이지로 리다이렉트: `Location: /posts/{postId}`
    *   `successMessage` 플래시 속성 포함
*   **실패 (HTTP 200 OK / 302 Found):**
    *   유효성 검증 실패 시 `comments/editForm.html` 뷰 반환.
    *   댓글이 없거나, 권한이 없는 경우 게시물 상세 페이지로 리다이렉트.
    *   `errorMessage` 플래시 속성 포함

<br>


### 4. 댓글 삭제 처리

*   **설명:** 특정 댓글 및 해당 댓글의 모든 답글을 삭제합니다.
*   **URI:** `/posts/{postId}/comments/{commentId}/delete`
*   **HTTP Method:** `POST`
*   **인증:** 필수 (로그인된 사용자 중 해당 댓글의 작성자)

#### 요청

*   **Path Variables:**
    *   `postId` (Long): 게시물 ID.
    *   `commentId` (Long): 삭제할 댓글의 고유 ID.
*   **Header:**
    *   `Cookie`: `SESSION` (로그인 세션 ID)

#### 응답

*   **성공 (HTTP 302 Found):**
    *   게시물 상세 페이지로 리다이렉트: `Location: /posts/{postId}`
    *   `successMessage` 플래시 속성 포함
*   **실패 (HTTP 302 Found):**
    *   댓글이 없거나, 게시물 ID와 댓글 ID 불일치, 삭제 권한 없음 시 게시물 상세 페이지로 리다이렉트.
    *   `errorMessage` 플래시 속성 포함
