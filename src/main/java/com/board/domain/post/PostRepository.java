package com.board.domain.post;

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
     * @param newPost 업데이트할 내용을 담은 게시물 객체
     */
    void update(Long id, Post newPost);

    /**
     * 지정된 ID의 게시물을 삭제합니다.
     * @param id 삭제할 게시물의 ID
     */
    void delete(Long id);

    /**
     * 모든 게시물을 조회합니다.
     * @return 모든 게시물 리스트
     */
    List<Post> findAll();

    /**
     * 지정된 ID의 게시물을 조회합니다.
     * @param id 조회할 게시물의 ID
     * @return 조회된 게시물 객체, 없으면 null
     */
    Post findById(Long id);
}