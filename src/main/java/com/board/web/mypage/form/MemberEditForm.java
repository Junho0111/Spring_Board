package com.board.web.mypage.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
public class MemberEditForm {

    /**회원의 이름*/
    @NotEmpty
    private String newName;

    /**회원의 비밀번호*/
    @NotEmpty
    private String newPassword;
}
