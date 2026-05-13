package com.board.domain.login.Kakao.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 카카오 사용자 정보 응답을 매핑하는 데이터 객체입니다.
 * 카카오로부터 받은 사용자의 고유 ID, 닉네임, 프로필 이미지 등을 포함합니다.
 */
public record KakaoUserInfoResponse(

    /** 카카오 고유 식별자 */
    Long id,

    /** 서비스 연결 시각 */
    @JsonProperty("connected_at") String connectedAt,

    /** 사용자 프로필 속성 */
    Properties properties,

    /** 카카오 계정 상세 정보 */
    @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {
    /**
     * 프로필 속성 정보
     */
    public record Properties(

        /** 닉네임 */
        String nickname,

        /** 프로필 이미지 URL */
        @JsonProperty("profile_image") String profileImage,

        /** 썸네일 이미지 URL */
        @JsonProperty("thumbnail_image") String thumbnailImage
    ) {}

    /**
     * 카카오 계정 상세 정보
     */
    public record KakaoAccount(

        /** 닉네임 제공 동의 필요 여부 */
        @JsonProperty("profile_nickname_needs_agreement") Boolean profileNicknameNeedsAgreement,

        /** 프로필 이미지 제공 동의 필요 여부 */
        @JsonProperty("profile_image_needs_agreement") Boolean profileImageNeedsAgreement,

        /** 프로필 정보 */
        Profile profile
    ) {
        /**
         * 프로필 상세 정보
         */
        public record Profile(

            /** 닉네임 */
            String nickname,

            /** 썸네일 이미지 URL */
            @JsonProperty("thumbnail_image_url") String thumbnailImageUrl,

            /** 프로필 이미지 URL */
            @JsonProperty("profile_image_url") String profileImageUrl,

            /** 기본 이미지 여부 */
            @JsonProperty("is_default_image") Boolean isDefaultImage
        ) {}
    }
}
