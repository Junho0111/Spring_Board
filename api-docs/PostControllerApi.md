# PostController API 명세서

## 개요
이 컨트롤러는 게시물 관련 요청(목록 조회, 상세 조회, 생성, 수정, 삭제) 및 파일(이미지, 첨부파일) 다운로드 요청을 처리합니다.

## 기본 URI
`/posts`

## API 목록

### 1. 이미지 다운로드

*   **설명:** 서버에 저장된 이미지 파일을 직접 반환합니다. 웹 페이지 내 `<img>` 태그의 `src` 속성으로 사용됩니다.
*   **URI:** `/posts/images/{filename}`
*   **HTTP Method:** `GET`
*   **인증:** 필요 없음

#### 요청

*   **Path Variables:**
    *   `filename` (String): 서버에 저장된 이미지 파일의 고유 이름.

#### 응답

*   **성공 (HTTP 200 OK):**
    *   이미지 파일의 바이너리 데이터. `Content-Type` 헤더는 이미지 유형에 따라 설정됩니다.
*   **실패:**
    *   파일을 찾을 수 없거나 접근 권한이 없는 경우 (서버 내부 로그만 기록될 수 있음).

<br>


### 2. 첨부 파일 다운로드

*   **설명:** 게시물에 첨부된 파일을 다운로드합니다. 브라우저가 파일을 직접 표시하는 대신 다운로드 대화상자를 표시합니다.
*   **URI:** `/posts/attach/{postId}`
*   **HTTP Method:** `GET`
*   **인증:** 필요 없음

#### 요청

*   **Path Variables:**
    *   `postId` (Long): 파일을 다운로드할 게시물의 ID.

#### 응답

*   **성공 (HTTP 200 OK):**
    *   `Content-Disposition: attachment; filename="{encodedUploadFileName}"` 헤더와 함께 파일의 바이너리 데이터 반환.
*   **실패:**
    *   게시물이나 첨부 파일을 찾을 수 없는 경우 (내부적으로 로그만 남기고 에러 처리될 수 있음).

<br>


### 3. 모든 게시물 목록 조회

*   **설명:** 시스템에 등록된 모든 게시물의 목록을 조회하여 뷰에 전달합니다.
*   **URI:** `/posts`
*   **HTTP Method:** `GET`
*   **인증:** 선택적 (로그인 여부에 따라 뷰 내용이 달라질 수 있음)

#### 요청

*   **Header:**
    *   `Cookie`: `SESSION` (로그인된 경우 세션 ID 포함)
*   **Body:** 없음

#### 응답

*   **성공 (HTTP 200 OK):**
    *   `posts/posts.html` 뷰 페이지 반환.
    *   Model 포함: `posts` (게시물 목록), `loginMember` (로그인 회원 정보, null일 수 있음).

<br>


### 4. 특정 게시물 상세 조회

*   **설명:** 특정 게시물의 상세 내용을 조회하고, 해당 게시물에 달린 댓글 목록 및 새 댓글 작성을 위한 폼을 함께 제공합니다.
*   **URI:** `/posts/{postId}`
*   **HTTP Method:** `GET`
*   **인증:** 필요 없음

#### 요청

*   **Path Variables:**
    *   `postId` (Long): 조회할 게시물의 고유 ID.

#### 응답

*   **성공 (HTTP 200 OK):**
    *   `posts/post.html` 뷰 페이지 반환.
    *   Model 포함: `post` (게시물 상세 정보), `comments` (댓글 목록), `commentForm` (빈 댓글 폼 객체).
*   **실패 (HTTP 302 Found):**
    *   게시물을 찾을 수 없는 경우 게시물 목록 페이지로 리다이렉트: `Location: /posts`

<br>


### 5. 게시물 추가 폼 제공

*   **설명:** 새로운 게시물을 작성하기 위한 폼 페이지를 제공합니다.
*   **URI:** `/posts/add`
*   **HTTP Method:** `GET`
*   **인증:** 필수 (로그인된 사용자)

#### 요청

*   **Header:**
    *   `Cookie`: `SESSION` (로그인 세션 ID)
*   **Body:** 없음

#### 응답

*   **성공 (HTTP 200 OK):**
    *   `posts/addForm.html` 뷰 페이지 반환.
    *   Model 포함: `post` (게시물 폼 데이터 바인딩을 위한 빈 객체).

<br>


### 6. 새 게시물 생성 처리

*   **설명:** 사용자가 제출한 폼 데이터를 바탕으로 새 게시물을 생성하고 저장합니다. 첨부 파일 및 이미지 파일 업로드를 지원합니다.
*   **URI:** `/posts/add`
*   **HTTP Method:** `POST`
*   **인증:** 필수 (로그인된 사용자)

#### 요청

*   **Header:**
    *   `Cookie`: `SESSION` (로그인 세션 ID)
*   **Form Data (`multipart/form-data`):**
    *   `title` (String, 필수): 게시물 제목.
    *   `content` (String, 필수): 게시물 내용.
    *   `attachFile` (MultipartFile, 선택): 단일 첨부 파일.
    *   `imageFiles` (List<MultipartFile>, 선택): 여러 이미지 파일.

#### 응답

*   **성공 (HTTP 302 Found):**
    *   생성된 게시물 상세 페이지로 리다이렉트: `Location: /posts/{postId}`
*   **실패 (HTTP 200 OK):**
    *   `posts/addForm.html` 뷰 페이지 반환.
    *   Model에 유효성 검사 실패 등의 에러 메시지 포함.

<br>


### 7. 게시물 수정 폼 제공

*   **설명:** 기존 게시물의 내용을 수정하기 위한 폼 페이지를 제공합니다.
*   **URI:** `/posts/{postId}/edit`
*   **HTTP Method:** `GET`
*   **인증:** 필수 (로그인된 사용자 중 해당 게시물의 작성자)

#### 요청

*   **Path Variables:**
    *   `postId` (Long): 수정할 게시물의 고유 ID.
*   **Header:**
    *   `Cookie`: `SESSION` (로그인 세션 ID)

#### 응답

*   **성공 (HTTP 200 OK):**
    *   `posts/editForm.html` 뷰 페이지 반환.
    *   Model 포함: `postForm` (수정 폼 데이터 바인딩 객체, 기존 내용으로 채워짐), `post` (기존 게시물 정보), `postId`.
*   **실패 (HTTP 302 Found):**
    *   게시물을 찾을 수 없거나 수정 권한이 없는 경우 `/posts` 또는 `/posts/{postId}`로 리다이렉트.
    *   `errorMessage` 플래시 속성 포함.

<br>


### 8. 게시물 수정 처리

*   **설명:** 사용자가 제출한 폼 데이터를 바탕으로 기존 게시물의 내용을 수정합니다. 파일 업로드(교체)를 지원합니다.
*   **URI:** `/posts/{postId}/edit`
*   **HTTP Method:** `POST`
*   **인증:** 필수 (로그인된 사용자 중 해당 게시물의 작성자)

#### 요청

*   **Path Variables:**
    *   `postId` (Long): 수정할 게시물의 고유 ID.
*   **Header:**
    *   `Cookie`: `SESSION` (로그인 세션 ID)
*   **Form Data (`multipart/form-data`):**
    *   `title` (String, 필수): 수정할 게시물 제목.
    *   `content` (String, 필수): 수정할 게시물 내용.
    *   `attachFile` (MultipartFile, 선택): 새로운 단일 첨부 파일 (기존 파일 교체).
    *   `imageFiles` (List<MultipartFile>, 선택): 새로운 여러 이미지 파일 (기존 파일 교체).

#### 응답

*   **성공 (HTTP 302 Found):**
    *   수정된 게시물 상세 페이지로 리다이렉트: `Location: /posts/{postId}`
*   **실패 (HTTP 200 OK / 302 Found):**
    *   유효성 검증 실패 시 `posts/editForm.html` 뷰 반환.
    *   게시물을 찾을 수 없거나 권한이 없는 경우 `/posts` 또는 `/posts/{postId}`로 리다이렉트.
    *   `errorMessage` 플래시 속성 포함.

<br>


### 9. 게시물 삭제 처리

*   **설명:** 특정 게시물을 삭제합니다. 게시물의 작성자만 삭제할 수 있습니다.
*   **URI:** `/posts/{postId}/delete`
*   **HTTP Method:** `POST`
*   **인증:** 필수 (로그인된 사용자 중 해당 게시물의 작성자)

#### 요청

*   **Path Variables:**
    *   `postId` (Long): 삭제할 게시물의 고유 ID.
*   **Header:**
    *   `Cookie`: `SESSION` (로그인 세션 ID)

#### 응답

*   **성공 (HTTP 302 Found):**
    *   게시물 목록 페이지로 리다이렉트: `Location: /posts`
    *   `successMessage` 플래시 속성 포함.
*   **실패 (HTTP 302 Found):**
    *   게시물을 찾을 수 없거나 삭제 권한이 없는 경우 `/posts` 또는 `/posts/{postId}`로 리다이렉트.
    *   `errorMessage` 플래시 속성 포함.
