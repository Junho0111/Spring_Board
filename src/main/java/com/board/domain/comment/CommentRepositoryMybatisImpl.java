package com.board.domain.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MyBatis를 사용하여 데이터베이스의 Comment 테이블에 접근하는 구현체입니다.
 * {@link CommentRepository} 인터페이스를 구현하며, 실제 쿼리는 {@link CommentRepositoryMybatis} 매퍼에 위임합니다.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CommentRepositoryMybatisImpl implements CommentRepository {

    private final CommentRepositoryMybatis commentMapper;

    /**
     * 새로운 댓글을 저장합니다.
     * 생성 시각과 수정 시각을 현재 시간으로 설정한 후 매퍼를 호출합니다.
     * @param comment 저장할 댓글 객체
     * @return 저장된 댓글 객체 (생성된 ID 포함)
     */
    @Override
    public Comment save(Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        commentMapper.save(comment);
        log.info("MyBatis SAVE [ID={}, Author={}]", comment.getId(), comment.getAuthor());
        return comment;
    }

    /**
     * 특정 댓글의 내용을 업데이트합니다.
     * @param id 업데이트할 댓글 ID
     * @param content 변경할 내용
     * @throws IllegalArgumentException 해당 ID의 댓글이 존재하지 않을 경우 발생
     */
    @Override
    public void update(Long id, String content) {
        int updated = commentMapper.update(id, content);
        if (updated == 0) {
            log.error("UPDATE FAILED: ID {} NOT FOUND", id);
            throw new IllegalArgumentException("수정 실패: 해당 ID(" + id + ")의 댓글이 존재하지 않습니다.");
        }
    }

    /**
     * 특정 댓글의 작성자 이름을 업데이트합니다.
     * @param id 수정할 댓글의 고유 ID
     * @param author 변경할 새 작성자 이름
     */
    @Override
    public void updateAuthor(Long id, String author) {
        commentMapper.updateAuthor(id, author);
    }

    /**
     * 고유 식별자(ID)를 기준으로 댓글을 삭제합니다.
     * @param id 삭제할 댓글 ID
     * @return 삭제되기 전의 댓글 객체 정보
     */
    @Override
    public Comment delete(Long id) {
        Comment comment = findById(id);
        if (comment != null) {
            commentMapper.delete(id);
            log.info("MyBatis DELETED [ID={}]", id);
        }
        return comment;
    }

    /**
     * 제공된 ID 리스트에 해당하는 모든 댓글을 일괄 삭제합니다.
     * MyBatis의 foreach 기능을 활용하여 성능을 최적화하였습니다.
     * @param commentIds 삭제할 댓글 ID 리스트
     */
    @Override
    public void deleteAllByIds(List<Long> commentIds) {
        if (commentIds == null || commentIds.isEmpty()) return;
        commentMapper.deleteAllByIds(commentIds);
        log.info("MyBatis BULK DELETED [Count={}]", commentIds.size());
    }

    /**
     * 모든 댓글 목록을 조회합니다.
     * @return 전체 댓글 리스트
     */
    @Override
    public List<Comment> findAll() {
        return commentMapper.findAll();
    }

    /**
     * ID를 기준으로 댓글 하나를 조회합니다.
     * @param id 조회할 댓글 ID
     * @return 조회된 댓글 객체
     */
    @Override
    public Comment findById(Long id) {
        return commentMapper.findById(id);
    }

    /**
     * 특정 게시물에 달린 모든 댓글을 조회합니다.
     * @param postId 조회할 게시물 ID
     * @return 해당 게시물의 댓글 리스트
     */
    @Override
    public List<Comment> findAllByPostId(Long postId) {
        return commentMapper.findAllByPostId(postId);
    }

    /**
     * 특정 게시물의 모든 댓글을 일괄 삭제합니다.
     * @param postId 삭제할 댓글들의 소속 게시물 ID
     */
    @Override
    public void deleteByPostId(Long postId) {
        commentMapper.deleteByPostId(postId);
        log.info("MyBatis ALL COMMENTS DELETED FOR POST [PostID={}]", postId);
    }

    /**
     * 부모 댓글 하위의 모든 자식/자손 댓글 ID를 조회합니다.
     * DB의 Recursive CTE 기능을 사용하여 효율적으로 탐색합니다.
     * @param parentCommentId 최상위 부모 댓글 ID
     * @return 자손 ID 리스트
     */
    @Override
    public List<Long> findAllDescendantCommentIds(Long parentCommentId) {
        return commentMapper.findAllDescendantCommentIds(parentCommentId);
    }
}
