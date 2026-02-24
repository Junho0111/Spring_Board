package com.board.domain.post;

import com.board.domain.uploadfile.UploadFile;

import java.util.List;

/**게시물(Post) 데이터에 접근하기 위한 리포지토리 인터페이스.*/
public interface PostRepository {

    /**
     * 새로운 게시물을 저장합니다.
     * @param post 저장할 게시물 객체
     * @return 저장된 게시물 객체 (주로 ID가 할당된 객체)
     */
    Post save(Post post);

    /**
     * 지정된 ID의 게시물을 업데이트합니다.
     * @param id 업데이트할 게시물의 ID
     * @param title 업데이트할 게시물 제목
     * @param content 업데이트할 게시물 내용
     * @param attachFile 첨부 파일
     * @param imageFiles 이미지 파일 목록
     */
    void update(Long id, String title, String content, UploadFile attachFile, List<UploadFile> imageFiles);

    /**
     * 지정된 ID의 게시물 작성자명을 업데이트합니다.
     *
     * @param id 업데이트할 회원 ID
     * @param author 업데이트할 회원 이름
     */
    void updateAuthor(Long id, String author);

    /**
     * 지정된 ID의 게시물을 삭제합니다.
     * @param id 삭제할 게시물의 ID
     */
    Post delete(Long id);

    /**
     * 모든 게시물을 조회합니다.
     * @return 모든 게시물 리스트
     */
    List<Post> findAll();

    /**
     * 검색 조건과 페이징 설정을 기준으로 게시물 목록을 조회합니다.
     *
     * @param type 조회할 타입 (author, title 등)
     * @param keyword 검색할 키워드 (null 또는 공백일 경우 전체 조회)
     * @param currentPage 현재 페이지 번호 (1부터 시작)
     * @param postsPerPage 한 페이지에 보여줄 게시물 수
     * @return 검색 조건 및 페이징이 적용된 게시물 리스트
     */
    List<Post> postSearchFindAll(String type, String keyword, int currentPage, int postsPerPage);

    /**
     * 지정된 ID의 게시물을 조회합니다.
     * @param id 조회할 게시물의 ID
     * @return 조회된 게시물 객체, 없으면 null
     */
    Post findById(Long id);

    /**
     * 특정 회원이 작성한 모든 게시물을 조회합니다.
     * @param memberId 조회할 회원의 ID
     * @return 해당 회원이 작성한 게시물 리스트
     */
    List<Post> findByMemberId(Long memberId);
}