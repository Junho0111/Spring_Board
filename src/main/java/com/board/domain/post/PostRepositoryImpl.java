package com.board.domain.post;

import com.board.domain.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        post.setId(++sequence); // ID 할당 및 증가
        store.put(post.getId(), post); // Map에 저장
        log.info("SAVE [ID={}, Author={}]", post.getId(), post.getAuthor());
        return post;
    }

    /**
     * 지정된 ID의 게시물 정보를 업데이트합니다.
     * 현재 구현에서는 제목과 내용만 업데이트합니다.
     *
     * @param id      업데이트할 게시물의 ID
     * @param newPost 업데이트할 내용을 담은 게시물 객체 (제목, 내용)
     */
    @Override
    public void update(Long id, Post newPost) {
        Post findPost = findById(id);
        if (findPost != null) {
            findPost.setTitle(newPost.getTitle());
            findPost.setContent(newPost.getContent());
            log.info("UPDATED [ID={}, Title={}, Content={}]", id, findPost.getTitle(), findPost.getContent());
        } else {
            log.info("UPDATE[FAILE] ID={} NOT FOUND.", id);
        }
    }

    /**
     * 지정된 ID의 게시물을 저장소에서 삭제합니다.
     *
     * @param id 삭제할 게시물의 ID
     */
    @Override
    public void delete(Long id) {
        String author = store.get(id).getAuthor();
        store.remove(id);
        log.info("DELETED [ID={}, Author={}]", id, author);
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

    public void clearStore() {
        store.clear();
    }
}
