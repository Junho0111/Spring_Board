package com.board.domain.comment;

import com.board.domain.post.Post;
import com.board.domain.post.PostRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class CommentRepositoryImplTest {

    CommentRepositoryImpl commentRepository = new CommentRepositoryImpl();

    PostRepositoryImpl postRepository = new PostRepositoryImpl();

    @AfterEach
    void afterEach() {
        commentRepository.clearStore();
    }

    @Test
    void 댓글_대댓글_등록() {
        //given
        Post post = new Post("test", "test", "test");
        postRepository.save(post);

        Comment comment = new Comment(post.getId(), "댓글_작성자", "댓글내용");
        commentRepository.save(comment);

        //when
        Comment reply = new Comment(post.getId(), comment.getId(), "대댓글_작성자", "대댓글내용");
        commentRepository.save(reply);

        //then
        assertThat(commentRepository.findById(comment.getId())).isEqualTo(comment);
        assertThat(commentRepository.findById(comment.getId()).getContent()).isEqualTo(comment.getContent());

        assertThat(commentRepository.findById(reply.getId())).isEqualTo(reply);
        assertThat(commentRepository.findById(reply.getId()).getContent()).isEqualTo(reply.getContent());
    }

    @Test
    void 댓글_대댓글_수정_성공() {
        //given
        Post post = new Post("test", "test", "test");
        postRepository.save(post);

        Comment comment = new Comment(post.getId(), "댓글_작성자", "댓글내용");
        commentRepository.save(comment);

        Comment reply = new Comment(post.getId(), comment.getId(), "대댓글_작성자", "대댓글내용");
        commentRepository.save(reply);

        //when
        Comment updateComment = new Comment(post.getId(), "댓글_작성자", "새로운 댓글내용");
        commentRepository.update(comment.getId(), updateComment.getContent());

        Comment updateReply = new Comment(post.getId(), "대댓글_작성자", "새로운 대댓글내용");
        commentRepository.update(reply.getId(), updateReply.getContent());

        //then
        assertThat(commentRepository.findById(comment.getId()).getAuthor()).isEqualTo(updateComment.getAuthor());
        assertThat(commentRepository.findById(comment.getId()).getContent()).isEqualTo(updateComment.getContent());

        assertThat(commentRepository.findById(reply.getId()).getAuthor()).isEqualTo(updateReply.getAuthor());
        assertThat(commentRepository.findById(reply.getId()).getContent()).isEqualTo(updateReply.getContent());
    }

    @Test
    void 댓글_대댓글_수정_실패() {
        //given
        Long notCommentId = 999L;
        String updateContent = "수정용 내용";

        //when & then
        assertThatThrownBy(() -> {
            commentRepository.update(notCommentId, updateContent);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("수정 실패: 해당 ID(" + notCommentId + ")의 댓글이 존재하지 않습니다.");
    }

    @Test
    void 댓글_대댓글_삭제_성공() {
        //given
        Post post = new Post("test", "test", "test");
        postRepository.save(post);

        Comment comment = new Comment(post.getId(), "댓글_작성자", "댓글내용");
        commentRepository.save(comment);

        Comment reply = new Comment(post.getId(), comment.getId(), "대댓글_작성자", "대댓글내용");
        commentRepository.save(reply);

        //when
        Comment deleteComment = commentRepository.delete(comment.getId());
        Comment deleteReply = commentRepository.delete(reply.getId());

        //then
        assertThat(deleteComment.getId()).isEqualTo(comment.getId());
        assertThat(commentRepository.findById(deleteComment.getId())).isNull();

        assertThat(deleteReply.getId()).isEqualTo(reply.getId());
        assertThat(commentRepository.findById(deleteReply.getId())).isNull();
    }

    @Test
    void 댓글_대댓글_삭제_실패() {
        //given
        Long notCommentId = 999L;

        //when & then
        assertThatThrownBy(() -> {commentRepository.delete(notCommentId);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("삭제 실패: 해당 ID(" + notCommentId + ")의 댓글이 존재하지 않습니다.");
    }

    @Test
    void 모든_댓글_반환() {
        //given
        Post post = new Post("test", "test", "test");
        postRepository.save(post);

        Comment comment = new Comment(post.getId(), "댓글_작성자", "댓글내용");
        commentRepository.save(comment);

        Comment reply = new Comment(post.getId(), comment.getId(), "대댓글_작성자", "대댓글내용");
        commentRepository.save(reply);

        //when
        List<Comment> Comments = commentRepository.findAll();

        //then
        assertThat(Comments.size()).isEqualTo(2);
        assertThat(Comments).contains(comment, reply);
    }

    @Test
    void 특정게시물_모든_댓글_반환() {
        //given
        Post post = new Post("test", "test", "test");
        postRepository.save(post);

        Comment comment = new Comment(post.getId(), "댓글_작성자", "댓글내용");
        commentRepository.save(comment);

        Comment reply = new Comment(post.getId(), comment.getId(), "대댓글_작성자", "대댓글내용");
        commentRepository.save(reply);

        //when
        List<Comment> CommentsByPostId = commentRepository.findAllByPostId(post.getId());

        //given
        assertThat(CommentsByPostId.size()).isEqualTo(2);
        assertThat(CommentsByPostId).contains(comment, reply);
    }

    @Test
    void 댓글_대댓글_검색_ID() {
        //given
        Post post = new Post("test", "test", "test");
        postRepository.save(post);

        Comment comment = new Comment(post.getId(), "댓글_작성자", "댓글내용");
        commentRepository.save(comment);

        Comment reply = new Comment(post.getId(), comment.getId(), "대댓글_작성자", "대댓글내용");
        commentRepository.save(reply);

        //when
        Comment findComment = commentRepository.findById(comment.getId());
        Comment findReply = commentRepository.findById(reply.getId());

        //then
        assertThat(findComment).isEqualTo(comment);
        assertThat(findReply).isEqualTo(reply);
    }
}