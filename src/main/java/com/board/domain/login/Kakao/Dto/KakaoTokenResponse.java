package com.board.domain.login.Kakao.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 카카오 액세스 토큰 응답을 매핑하는 데이터 객체입니다.
 * OAuth2 인증 과정에서 카카오로부터 받은 토큰 정보를 포함합니다.
 */
public record KakaoTokenResponse(

    /** 액세스 토큰 */
    @JsonProperty("access_token") String accessToken,

    /** 토큰 타입 (Bearer) */
    @JsonProperty("token_type") String tokenType,

    /** 리프레시 토큰 */
    @JsonProperty("refresh_token") String refreshToken,

    /** 액세스 토큰 만료 시간 (초) */
    @JsonProperty("expires_in") Integer expiresIn,

    /** 인증 범위 */
    @JsonProperty("scope") String scope,

    /** 리프레시 토큰 만료 시간 (초) */
    @JsonProperty("refresh_token_expires_in") Integer refreshTokenExpiresIn
) {
}
