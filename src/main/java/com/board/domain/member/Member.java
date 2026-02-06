package com.board.domain.member;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Member {

    /**회원의 고유 식별자아이디*/
    private Long id;

    /**회원의 로그인 아이디*/
    @NotEmpty
    private String loginId;

    /**회원의 이름*/
    @NotEmpty
    private String name;

    /**회원의 비밀번호*/
    @NotEmpty
    private String password;


    public Member(String loginId, String name, String password) {
        this.loginId = loginId;
        this.name = name;
        this.password = password;
    }
}
