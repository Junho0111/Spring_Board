package com.board.domain.post;

import com.board.domain.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 게시물 리포지토리의 메모리 내 구현체입니다.
 * {@link PostRepository} 인터페이스를 구현합니다.
 */
@Slf4j
@Repository
public class PostRepositoryImpl implements PostRepository {

    /**
     * 게시물 데이터를 저장하는 스레드 안전한 Map입니다.
     * key는 게시물 ID, value는 Post 객체입니다.
     */
    private static final Map<Long, Post> store = new ConcurrentHashMap<>();

    /**
     * 게시물 ID를 생성하기 위한 시퀀스 번호입니다.
     * 동시성 문제를 고려하여 스레드 안전하게 증가시킵니다.
     */
    private static long sequence = 0L;

    /**
     * 새로운 게시물을 저장하고, 고유 ID를 할당합니다.
     *
     * @param post 저장할 게시물 객체
     * @return ID가 할당되어 저장된 게시물 객체
     */
    @Override
    public Post save(Post post) {
        post.setId(++sequence);
        store.put(post.getId(), post);
        log.info("SAVE [ID={}, Author={}, Title={}, AuthorId={}]", post.getId(), post.getAuthor(), post.getTitle(), post.getAuthorId());
        return post;
    }

    /**
     * 지정된 ID의 게시물 정보를 업데이트합니다.
     * 현재 구현에서는 제목과 내용만 업데이트합니다.
     *
     * @param id      업데이트할 게시물의 ID
     * @param title 업데이트할 게시물 제목
     * @param content 업데이트할 게시물 내용
     * @throws IllegalArgumentException 해당 ID의 게시물이 존재하지 않을 경우 발생
     */
    @Override
    public void update(Long id, String title, String content) {
        Post findPost = findById(id);

        if (findPost == null) {
            log.error("UPDATE FAILED: ID {} NOT FOUND", id);
            throw new IllegalArgumentException("수정 실패: 해당 ID(" + id + ")의 게시물이 존재하지 않습니다.");
        }

        findPost.setTitle(title);
        findPost.setContent(content);
        log.info("UPDATED [ID={}, Author={} ,Title={}]", id, findPost.getAuthor(), findPost.getTitle());
    }

    /**
     * 지정된 ID의 게시물을 저장소에서 삭제합니다.
     *
     * @param id 삭제할 게시물의 ID
     * @throws IllegalArgumentException 해당 ID의 게시물이 존재하지 않을 경우 발생
     * @return 삭제된 게시물 객체
     */
    @Override
    public Post delete(Long id) {
        Post deletePost = findById(id);

        if (deletePost == null) {
            log.error("DELETE FAILED: ID {} NOT FOUND", id);
            throw new IllegalArgumentException("삭제 실패: 해당 ID(" + id + ")의 게시물이 존재하지 않습니다.");
        }

        store.remove(id);
        log.info("DELETED [ID={}, AuthorId={}, Author={}, Title={}]", id, deletePost.getAuthorId(), deletePost.getAuthor(), deletePost.getTitle());
        return  deletePost;
    }

    /**
     * 저장소에 있는 모든 게시물을 리스트 형태로 반환합니다.
     *
     * @return 모든 게시물의 {@link List}
     */
    @Override
    public List<Post> findAll() {
        return new ArrayList<>(store.values());
    }

    /**
     * 지정된 ID에 해당하는 게시물을 찾아 반환합니다.
     *
     * @param id 조회할 게시물의 ID
     * @return 찾아진 게시물 객체, ID에 해당하는 게시물이 없으면 null 반환
     */
    @Override
    public Post findById(Long id) {
        return store.get(id);
    }

    /**
     * 특정 회원이 작성한 모든 게시물을 찾아 리스트 형태로 반환합니다.
     *
     * @param memberId 조회할 회원의 ID
     * @return 해당 회원이 작성한 게시물들의 {@link List}
     */
    @Override
    public List<Post> findByMemberId(Long memberId) {
        return store.values().stream()
                .filter(post -> Objects.equals(post.getAuthorId(), memberId))
                .toList();
    }

    public void clearStore() {
        store.clear();
    }
}
