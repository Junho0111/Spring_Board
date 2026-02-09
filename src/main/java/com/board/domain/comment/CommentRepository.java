package com.board.domain.comment;

import java.util.List;

/**댓글(Comment) 데이터에 접근하기 위한 리포지토리 인터페이스.*/
public interface CommentRepository {

    /**
     * 새로운 댓글을 저장합니다.
     * @param comment 저장할 댓글 객체
     * @return 저장된 댓글 객체 (주로 ID가 할당된 객체)
     */
    Comment save(Comment comment);

    /**
     * 지정된 ID의 댓글 내용을 업데이트합니다.
     * @param id 업데이트할 댓글의 ID
     * @param content 업데이트할 댓글 내용
     */
    void update(Long id, String content);

    /**
     * 지정된 ID의 댓글을 삭제합니다.
     * @param id 삭제할 댓글의 ID
     * @return 삭제된 댓글 객체
     */
    Comment delete(Long id);

    /**
     * 모든 댓글을 조회합니다.
     * @return 모든 댓글 리스트
     */
    List<Comment> findAll();

    /**
     * 지정된 ID의 댓글을 조회합니다.
     * @param id 조회할 댓글의 ID
     * @return 조회된 댓글 객체, 없으면 null
     */
    Comment findById(Long id);

    /**
     * 특정 게시물에 속한 모든 댓글을 조회합니다.
     * @param postId 댓글을 조회할 게시물의 ID
     * @return 특정 게시물의 댓글 리스트
     */
    List<Comment> findAllByPostId(Long postId);
}
