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

    /**네이버 고유 식별자 (OAuth 연동 시 사용)*/
    private String naverId;


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
     * 외부 소셜 로그인 공통 생성자
     * @param loginId 로그인 아이디
     * @param name 이름
     * @param password 비밀번호 (사용 안 함)
     * @param provider 서비스 제공자 ("kakao", "naver" 등)
     * @param socialId 해당 서비스의 고유 식별자
     */
    public Member(String loginId, String name, String password, String provider, String socialId) {
        this.loginId = loginId;
        this.name = name;
        this.password = password;

        if ("kakao".equals(provider)) {
            this.kakaoId = socialId;
        }

        if ("naver".equals(provider)) {
            this.naverId = socialId;
        }
    }
}
