package com.board.domain.post;

import com.board.domain.member.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


class PostRepositoryImplTest {

    PostRepositoryImpl postRepository = new PostRepositoryImpl();

    @AfterEach
    void afterEach() {
        postRepository.clearStore();
    }

    @Test
    void 게시글_등록() {
        //given
        Post post = new Post("test", "test", "testAuthor", 1L);

        //when
        postRepository.save(post);

        //then
        assertThat(postRepository.findById(post.getId())).isEqualTo(post);
    }

    @Test
    void 게시글_수정_성공() {
        //given
        Post oldPost = new Post("기존 제목", "기존 내용", "작성자", 1L);
        Post updatePost = new Post("수정될 제목", "수정될 내용", "작성자", 1L);
        postRepository.save(oldPost);

        //when
        postRepository.update(oldPost.getId(), updatePost.getTitle(), updatePost.getContent());

        //then
        assertThat(postRepository.findById(oldPost.getId()).getTitle()).isEqualTo(updatePost.getTitle());
        assertThat(postRepository.findById(oldPost.getId()).getContent()).isEqualTo(updatePost.getContent());
        assertThat(postRepository.findById(oldPost.getId()).getAuthorId()).isEqualTo(oldPost.getAuthorId());
    }

    @Test
    void 게시글_수정_실패() {
        // given
        Long notSaveId = 999L;
        String updateTitle = "수정용 제목";
        String updateContent = "수정용 내용";

        // when & then
        assertThatThrownBy(() -> {
            postRepository.update(notSaveId, updateTitle, updateContent);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("수정 실패: 해당 ID(" + notSaveId + ")의 게시물이 존재하지 않습니다.");
    }

    @Test
    void 게시글_삭제_성공() {
        // given
        Post post = new Post("삭제될 제목", "삭제될 내용", "작성자", 1L);
        postRepository.save(post);

        // when
        Post deletedPost = postRepository.delete(post.getId());

        // then
        assertThat(deletedPost.getId()).isEqualTo(post.getId());
        assertThat(postRepository.findById(post.getId())).isNull();
        assertThat(postRepository.findAll()).isEmpty();
    }

    @Test
    void 게시글_삭제_실패() {
        // given
        Long notSaveId = 999L;

        // when & then
        assertThatThrownBy(() -> {
            postRepository.delete(notSaveId);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("삭제 실패: 해당 ID(" + notSaveId + ")의 게시물이 존재하지 않습니다.");
    }

    @Test
    void 모든_게시글_반환() {
        //given
        Post post = new Post("test", "test", "testAuthor1", 1L);
        Post post2 = new Post("test1", "test1", "testAuthor2", 2L);

        postRepository.save(post);
        postRepository.save(post2);

        //when
        List<Post> posts = postRepository.findAll();

        //then
        assertThat(posts.size()).isEqualTo(2);
        assertThat(posts).contains(post, post2);
    }

    @Test
    void 게시물_검색_ID() {
        //given
        Post post = new Post("test", "test", "testAuthor", 1L);

        //when
        Post savePost = postRepository.save(post);

        //then
        Post findMember = postRepository.findById(post.getId());
        assertThat(findMember).isEqualTo(savePost);
    }
}