package com.board.domain.login.Naver.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 네이버 사용자 정보 응답을 매핑하는 데이터 객체입니다.
 */
public record NaverUserInfoResponse(
    /** API 호출 결과 코드 */
    @JsonProperty("resultcode") String resultCode,

    /** 결과 메시지 */
    @JsonProperty("message") String message,

    /** 실제 사용자 정보 응답 상세 */
    @JsonProperty("response") Response response
) {
    /**
     * 네이버 사용자 상세 정보 응답 객체
     */
    public record Response(
        /** 네이버 고유 식별자 */
        String id,

        /** 사용자 별명 */
        String nickname,

        /** 사용자 실명 */
        String name,

        /** 사용자 이메일 */
        String email,

        /** 프로필 이미지 URL */
        @JsonProperty("profile_image") String profileImage
    ) {}
}
