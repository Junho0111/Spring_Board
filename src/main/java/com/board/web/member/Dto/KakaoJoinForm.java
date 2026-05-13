package com.board.web.member.Dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * 카카오 회원가입 시 사용자로부터 추가 정보를 입력받기 위한 폼 객체입니다.
 * 카카오 인증 후 전달받은 고유 식별자와 사용자가 직접 입력한 이름을 관리합니다.
 */
@Getter
@Setter
public class KakaoJoinForm {

    /** 카카오 고유 식별자 (hidden 필드로 유지) */
    @NotEmpty
    private String kakaoId;

    /** 서비스 내에서 사용할 사용자의 이름(닉네임) */
    @NotEmpty
    private String name;
}
