package com.board.web.member.Dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * 네이버 회원가입 전용 폼 객체입니다.
 */
@Getter
@Setter
public class NaverJoinForm {

    /** 네이버 고유 식별자 (hidden 필드로 유지) */
    @NotEmpty
    private String naverId;

    /** 서비스 내에서 사용할 사용자의 이름(닉네임) */
    @NotEmpty
    private String name;

    /** 서비스 내에서 사용할 비밀번호 */
    @NotEmpty
    private String password;
}
