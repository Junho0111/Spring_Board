package com.board.domain.comment;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * MyBatis 매퍼 인터페이스입니다.
 * commentMapper.xml에 정의된 SQL 쿼리와 연결됩니다.
 */
@Mapper
public interface CommentRepositoryMybatis {

    /**
     * 댓글을 저장합니다.
     * @param comment 저장할 댓글 객체
     */
    void save(Comment comment);

    /**
     * 댓글 내용을 업데이트합니다.
     * @param id 업데이트할 댓글 ID
     * @param content 변경할 내용
     * @return 영향받은 행의 수
     */
    int update(@Param("id") Long id, @Param("content") String content);

    /**
     * 댓글 작성자명을 업데이트합니다.
     * @param id 업데이트할 댓글 ID
     * @param author 변경할 작성자 이름
     */
    void updateAuthor(@Param("id") Long id, @Param("author") String author);

    /**
     * 댓글을 삭제합니다.
     * @param id 삭제할 댓글 ID
     */
    void delete(Long id);

    /**
     * 여러 댓글을 한 번에 삭제합니다.
     * @param commentIds 삭제할 ID 리스트
     */
    void deleteAllByIds(@Param("ids") List<Long> commentIds);

    /**
     * 모든 댓글을 조회합니다.
     * @return 댓글 리스트
     */
    List<Comment> findAll();

    /**
     * ID로 댓글을 조회합니다.
     * @param id 조회할 댓글 ID
     * @return 조회된 댓글 객체
     */
    Comment findById(Long id);

    /**
     * 게시물 ID로 모든 댓글을 조회합니다.
     * @param postId 게시물 ID
     * @return 해당 게시물의 댓글 리스트
     */
    List<Comment> findAllByPostId(Long postId);

    /**
     * 게시물 ID로 모든 댓글을 삭제합니다.
     * @param postId 게시물 ID
     */
    void deleteByPostId(Long postId);

    /**
     * 특정 부모 댓글의 모든 하위 자식 댓글 ID를 조회합니다. (Recursive)
     * @param parentCommentId 부모 댓글 ID
     * @return 자손 댓글 ID 리스트
     */
    List<Long> findAllDescendantCommentIds(Long parentCommentId);
}
