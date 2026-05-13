package com.board.domain.login.Naver.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 네이버 액세스 토큰 응답을 매핑하는 데이터 객체입니다.
 */
public record NaverTokenResponse(
    /** 액세스 토큰 */
    @JsonProperty("access_token") String accessToken,

    /** 리프레시 토큰 */
    @JsonProperty("refresh_token") String refreshToken,

    /** 토큰 타입 (Bearer) */
    @JsonProperty("token_type") String tokenType,

    /** 액세스 토큰 만료 시간 (초) */
    @JsonProperty("expires_in") String expiresIn,

    /** 에러 코드 */
    @JsonProperty("error") String error,

    /** 에러 메시지 */
    @JsonProperty("error_description") String errorDescription
) {
}
