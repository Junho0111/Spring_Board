package com.board.domain.member;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
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

    /**카카오 고유 식별자 (OAuth 연동 시 사용)*/
    private String kakaoId;


    /**
     * 기본 생성자
     * @param loginId 로그인 아이디
     * @param name 이름
     * @param password 비밀번호
     */
    public Member(String loginId, String name, String password) {
        this.loginId = loginId;
        this.name = name;
        this.password = password;
    }

    /**
     * 카카오 연동 전용 생성자
     * @param loginId 로그인 아이디 (임시 또는 카카오 식별자 활용)
     * @param name 이름 (카카오 닉네임)
     * @param password 비밀번호 (사용 안 함)
     * @param kakaoId 카카오 고유 식별자
     */
    public Member(String loginId, String name, String password, String kakaoId) {
        this.loginId = loginId;
        this.name = name;
        this.password = password;
        this.kakaoId = kakaoId;
    }
}
