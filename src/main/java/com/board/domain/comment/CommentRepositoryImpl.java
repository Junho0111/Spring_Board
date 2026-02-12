package com.board.domain.comment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 댓글 리포지토리의 메모리 내 구현체입니다.
 * {@link CommentRepository} 인터페이스를 구현합니다.
 */
@Slf4j
@Repository
public class CommentRepositoryImpl implements CommentRepository {

    /**
     * 댓글 데이터를 저장하는 스레드 안전한 Map입니다.
     * key는 댓글 ID, value는 Comment 객체입니다.
     */
    private static final Map<Long, Comment> store = new ConcurrentHashMap<>();

    /**
     * 댓글 ID를 생성하기 위한 시퀀스 번호입니다.
     * 동시성 문제를 고려하여 스레드 안전하게 증가시킵니다.
     */
    private static long sequence = 0L;

    /**
     * 새로운 댓글을 저장하고, 고유 ID를 할당합니다.
     *
     * @param comment 저장할 댓글 객체
     * @return ID가 할당되어 저장된 댓글 객체
     */
    @Override
    public Comment save(Comment comment) {
        comment.setId(++sequence);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        store.put(comment.getId(), comment);

        log.info("SAVE [ID={}, PostId={}, ParentId={}, Author={}, AuthorId={}]", comment.getId(), comment.getPostId(), comment.getParentCommentId(), comment.getAuthor(), comment.getAuthorId());
        return comment;
    }

    /**
     * 지정된 ID의 댓글 내용을 업데이트합니다.
     * 현재 구현에서는 내용만 업데이트하고 updatedAt을 갱신합니다.
     *
     * @param id      업데이트할 댓글의 ID
     * @param content 업데이트할 댓글 내용
     * @throws IllegalArgumentException 해당 ID의 댓글이 존재하지 않을 경우 발생
     */
    @Override
    public void update(Long id, String content) {
        Comment findComment = findById(id);

        if (findComment == null) {
            log.error("UPDATE FAILED: ID {} NOT FOUND", id);
            throw new IllegalArgumentException("수정 실패: 해당 ID(" + id + ")의 댓글이 존재하지 않습니다.");
        }

        findComment.setContent(content);
        findComment.setUpdatedAt(LocalDateTime.now());
        log.info("UPDATED [ID={}, Content={}]", id, findComment.getContent());
    }

    /**
     * 지정된 ID의 댓글 작성자명을 업데이트합니다.
     *
     * @param id 업데이트할 회원 ID
     * @param author 업데이트할 회원 이름
     */
    @Override
    public void updateAuthor(Long id, String author) {
        Comment findComment = findById(id);

        if (findComment == null) {
            log.error("UPDATE FAILED: ID {} NOT FOUND", id);
            throw new IllegalArgumentException("수정 실패: 해당 ID(" + id + ")의 댓글이 존재하지 않습니다.");
        }

        findComment.setAuthor(author);
        log.info("AUTHOR UPDATED [ID={}, Author={}]", id, findComment.getAuthor());
    }

    /**
     * 지정된 ID의 댓글을 저장소에서 삭제합니다.
     *
     * @param id 삭제할 댓글의 ID
     * @throws IllegalArgumentException 해당 ID의 댓글이 존재하지 않을 경우 발생
     * @return 삭제된 댓글 객체
     */
    @Override
    public Comment delete(Long id) {
        Comment deleteComment = findById(id);

        if (deleteComment == null) {
            log.error("DELETE FAILED: ID {} NOT FOUND", id);
            throw new IllegalArgumentException("삭제 실패: 해당 ID(" + id + ")의 댓글이 존재하지 않습니다.");
        }

        store.remove(id);
        log.info("DELETED [ID={}, PostId={}, Author={}]", id, deleteComment.getPostId(), deleteComment.getAuthor());
        return deleteComment;
    }

    /**
     * 여러 개의 댓글 ID를 한 번에 삭제합니다.
     * @param commentIds 삭제할 댓글의 ID 리스트
     */
    @Override
    public void deleteAllByIds(List<Long> commentIds) {
        for (Long id : commentIds) {
            Comment deletedComment = store.remove(id);

            if (deletedComment != null) {
                log.info("삭제 [ID={}, PostId={}, Author={}]", id, deletedComment.getPostId(), deletedComment.getAuthor());
            } else {
                log.warn("삭제 실패: ID {} NOT FOUND", id);
            }
        }
    }

    /**
     * 저장소에 있는 모든 댓글을 리스트 형태로 반환합니다.
     *
     * @return 모든 댓글의 {@link List}
     */
    @Override
    public List<Comment> findAll() {
        return new ArrayList<>(store.values());
    }

    /**
     * 지정된 ID에 해당하는 댓글을 찾아 반환합니다.
     *
     * @param id 조회할 댓글의 ID
     * @return 찾아진 댓글 객체, ID에 해당하는 댓글이 없으면 null 반환
     */
    @Override
    public Comment findById(Long id) {
        return store.get(id);
    }

    /**
     * 특정 게시물에 속한 모든 댓글을 찾아 리스트 형태로 반환합니다.
     *
     * @param postId 댓글을 조회할 게시물의 ID
     * @return 특정 게시물의 댓글 리스트
     */
    @Override
    public List<Comment> findAllByPostId(Long postId) {
        return store.values().stream()
                .filter(comment -> comment.getPostId().equals(postId))
                .collect(Collectors.toList());
    }

    /**
     * 특정 게시물에 속한 모든 댓글 지움
     *
     * @param postId 댓글을 조회할 게시물의 ID
     */
    @Override
    public void deleteByPostId(Long postId) {
        List<Comment> comments = findAllByPostId(postId);
        for (Comment comment : comments) {
            delete(comment.getId());
        }
    }

    /**
     * 특정 부모 댓글 ID를 가진 모든 자식 댓글의 ID를 재귀적으로 조회하여 반환합니다.
     * (직계 자식뿐만 아니라 모든 하위 댓글 포함)
     *
     * @param parentCommentId 조회할 부모 댓글의 ID
     * @return 모든 하위 댓글의 ID 리스트 (부모 댓글 자체 ID는 포함되지 않음)
     */
    @Override
    public List<Long> findAllDescendantCommentIds(Long parentCommentId) {
        List<Long> descendantIds = new ArrayList<>();
        findDescendants(parentCommentId, descendantIds);
        return descendantIds;
    }

    /**
     * 특정 부모 ID를 가진 댓글의 모든 하위 댓글을 재귀적으로 찾아 수집합니다.
     * 이 메서드는 DFS방식으로 댓글 트리를 탐색합니다.
     *
     * @param parentId 현재 탐색 중인 부모 댓글의 ID
     * @param descendantIds 발견된 모든 하위 댓글 ID를 수집할 리스트
     */
    private void findDescendants(Long parentId, List<Long> descendantIds) {
        List<Comment> directChildren = store.values().stream()
                .filter(comment -> Objects.equals(comment.getParentCommentId(), parentId))
                .toList();

        for (Comment c : directChildren) {
            descendantIds.add(c.getId());
            findDescendants(c.getId(), descendantIds);
        }
    }

    public void clearStore() {
        store.clear();
    }
}
