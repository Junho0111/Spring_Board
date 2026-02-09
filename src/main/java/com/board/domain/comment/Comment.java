package com.board.domain.comment;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    /** 댓글 고유 ID */
    private Long id;

    /** 연결된 게시물 ID */
    private Long postId;

    /** 댓글 작성자 */
    private String author;

    /** 댓글 내용 */
    private String content;

    /** 댓글 생성 시간 */
    private LocalDateTime createdAt;

    /** 댓글 수정 시간 */
    private LocalDateTime updatedAt;

    public Comment(Long postId, String author, String content) {
        this.postId = postId;
        this.author = author;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
