package com.board.web.comment.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/** 댓글 및 대댓글 생성을 위한 폼 데이터 전송 객체 (DTO). */
@Data
public class CommentForm {

    @NotEmpty(message = "댓글 내용을 입력해주세요.")
    private String content;

    // 대댓글인 경우 부모 댓글의 ID
    private Long parentCommentId;
}
