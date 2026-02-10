package com.board.domain.comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Comment {

    /** 댓글 고유 ID */
    private Long id;

    /** 연결된 게시물 ID */
    private Long postId;

    /** 부모 댓글 ID (대댓글인 경우) */
    private Long parentCommentId;

    /** 댓글 작성자 */
    private String author;

    /** 댓글 작성자 ID */
    private Long authorId;

    /** 댓글 내용 */
    private String content;

    /** 댓글 생성 시간 */
    private LocalDateTime createdAt;

    /** 댓글 수정 시간 */
    private LocalDateTime updatedAt;

    // 댓글
    public Comment(Long postId, String author, Long authorId, String content) {
        this.postId = postId;
        this.author = author;
        this.authorId = authorId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 대댓글
    public Comment(Long postId, Long parentCommentId, String author, Long authorId, String content) {
        this.postId = postId;
        this.parentCommentId = parentCommentId;
        this.author = author;
        this.authorId = authorId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
