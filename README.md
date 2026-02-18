# Spring Boot 게시판 프로젝트 (Board Project)

본 프로젝트는 Java와 Spring Boot를 활용하였으며<br> 게시판의 핵심 기능을 구현하고, 개발 과정에서 발생하는 성능 최적화 및 보안 이슈를 해결해보는 프로젝트입니다.

---

## 아키텍처 (Architecture)

```mermaid
graph LR

    subgraph Client ["사용자 영역"]
        Browser[<font size=5>🌐</font><br>브라우저]
    end

    subgraph WebLayer ["프레젠테이션 계층 (Web)"]
        direction TB
        DS[DispatcherServlet]
        
        subgraph Interceptors ["검문소 (Interceptors)"]
            Log[로그 기록]
            Login[로그인 체크]
        end

        subgraph Controllers ["컨트롤러 (업무 배정)"]
            C_Home[홈]
            C_Login[로그인]
            C_Post[게시글]
            C_Comment[댓글]
        end

        DTO["📦 데이터 전달 객체<br>(Forms/DTOs)"]
    end

    subgraph ServiceLayer ["비즈니스 계층 (Service)"]
        S_Member[회원 서비스]
        S_Login[로그인 서비스]
    end

    subgraph RepositoryLayer ["데이터 접근 계층 (Repository)"]
        R_Post[게시글 저장소]
        R_Member[회원 저장소]
    end

    subgraph Infra ["인프라 / 저장소"]
        DB[(💾 메모리 DB)]
        Files[📁 로컬 파일 시스템]
    end

    Browser ==> DS
    DS --> Log --> Login --> Controllers
    
    Controllers -.-> DTO
    Controllers ==> ServiceLayer
    ServiceLayer ==> RepositoryLayer
    
    RepositoryLayer --> DB
    ServiceLayer --> Files
    
    
---
    
    
## 개발 원칙

* **엔티티 설계의 중요성:** 엔티티 변수명의 일관성이 타임리프(`ModelAttribute`)와 컨트롤러 간의 데이터 전달 효율을 결정한다는 것을 체감했습니다.
* **엔티티 설계와 일관성:** 엔티티 변수명의 일관성이 타임리프와 컨트롤러 간의 데이터 전달 효율을 결정하며, 잘 지은 변수명 하나가 불필요한 디버깅 시간을 획기적으로 줄여준다는 것을 체감했습니다.
* **고유 식별자(ID) 중심 설계:** 닉네임과 같이 변경 가능하고 중복될 수 있는 속성이 아닌, 시스템 고유 식별자를 기준으로 모든 로직(CRUD)을 설계해야 보안 취약점을 방지할 수 있음을 배웠습니다.
* **계층 구조의 이해와 무결성:** 댓글과 대댓글 사이의 트리 구조 데이터를 처리하기 위해 재귀 알고리즘(DFS)을 도입했으며, 부모 삭제 시 자식 노드가 고립되지 않도록 하는 데이터 무결성 보장의 중요성을 학습했습니다.

---

## 문제와 해결 사례

### 1. 사용자 식별 오류를 통한 권한 검증 취약점 해결

* **문제:** 로그인 시 닉네임 중복을 허용함에도 불구하고, 수정/삭제 권한을 `Name`으로 비교하여 타인의 게시물을 제어할 수 있는 보안 취약점 발생.
* **해결:** 엔티티 구조를 개선하여 `MemberId` 필드를 추가하고, 검증 로직을 세션의 고유 ID와 Repository의 `authorId`를 비교하는 방식으로 변경하여 보안성을 확보했습니다.
<br>

### 2. 댓글 삭제 시 N+1 문제 및 Batch 처리 최적화

* **문제:** 게시글 삭제 시 연관된 수십 개의 댓글을 개별적으로 삭제하면서 데이터량에 비례해 비효율적으로 동작하는 성능 저하 확인.
* **해결:** 삭제 대상 ID를 리스트로 수집한 뒤, 한 번의 요청으로 일괄 삭제하는 `deleteAllByIds` (Batch Delete)를 구현하여 현재는 Repository에서의 문제지만 향후 나타날 DB 네트워크 오버헤드 문제를 해결하였습니다.
<br>

### 3. 계층형 댓글(Tree)의 고립 데이터(Orphan) 문제

* **문제:** 부모 댓글 삭제 시 직계 자식만 삭제되고, 그 하위(손자) 댓글들이 Repository(DB)에 남거나 화면에 비정상 출력되는 현상.
* **원인:** 기존 로직이 최상위 댓글인 경우에만 하위 탐색을 하도록 설계되어 깊은 계층의 데이터를 누락함.
* **해결:** `Recursive Algorithm(DFS)`을 도입하여 특정 노드 하위의 모든 후손 ID를 끝까지 추적하는 `findAllDescendantCommentIds` 로직을 구현, '폭포수형 삭제'를 완성하여 데이터 무결성을 보장했습니다.
<br>
 
### 4. 회원 정보 변경 시 권한 인식 불일치 버그

* **문제:** 회원 정보를 수정한 후, 본인이 이전에 작성한 게시물이나 댓글을 수정/삭제하지 못하는 문제 발생.
* **원인:** 프론트엔드(Thymeleaf) 조건문에서 세션의 닉네임과 게시글의 작성자명을 비교하고 있었음.
* **해결:** 비교 대상을 고유 식별자(`ID`)로 통일하여 회원 정보(닉네임 등)가 변경되어도 본인 인증이 정확히 유지되도록 수정했습니다.
```html
<div th:if="${session.loginMember.name == comment.author}">
<div th:if="${session.loginMember.id == comment.authorId}">

```
<br>

### 5. 뷰 템플릿 경로 매핑 휴먼 에러

* **문제:** 컨트롤러의 반환 경로와 실제 타임리프 파일 경로 불일치로 인한 404 에러 반복.
* **해결:** 경로 명명 규칙을 표준화하고, 템플릿 구조를 도메인별(post, member, comment)로 명확히 분리하여 관리함으로써 해결하였습니다.

---

## Tech Stack

* **Language:** Java 22
* **Framework:** Spring Boot
* **Template Engine:** Thymeleaf
* **Build Tool:** Gradle
* **Library & Tools:**
    - Lombok: 어노테이션 기반 코드 자동 생성 및 slf4j를 이용한 로그 관리
    - Gradle: 프로젝트 빌드 및 의존성 관리
---

## Future Works

* [ ] 외부 데이터베이스 연동(나아가 JPA사용까지)
* [ ] 로그 레벨 분리 및 로그 관리 시스템 구축

---
