package com.board.domain.login.Naver;

import com.board.domain.login.Naver.Dto.NaverTokenResponse;
import com.board.domain.login.Naver.Dto.NaverUserInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 네이버 OAuth2 API와 통신을 담당하는 서비스 클래스입니다.
 * 액세스 토큰 발급 및 사용자 정보 조회 기능을 제공합니다.
 */
@Slf4j
@Service
public class NaverService {

    /** 네이버 Client ID */
    @Value("${naver.client.id}")
    private String clientId;

    /** 네이버 Client Secret */
    @Value("${naver.client.secret}")
    private String clientSecret;

    /** 네이버 Redirect URI */
    @Value("${naver.redirect.uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 네이버로부터 받은 인가 코드를 사용하여 액세스 토큰을 요청합니다.
     * @param code 네이버 인증 서버에서 발급한 인가 코드
     * @param state 상태 토큰 (보안 및 의도 구분용)
     * @return 발급된 액세스 토큰 문자열
     */
    public String getAccessToken(String code, String state) {
        String tokenUrl = "https://nid.naver.com/oauth2.0/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("state", state);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        NaverTokenResponse response = restTemplate.postForObject(tokenUrl, request, NaverTokenResponse.class);

        if (response != null && response.accessToken() != null) {
            return response.accessToken();
        }
        return null;
    }

    /**
     * 액세스 토큰을 사용하여 네이버 사용자 정보를 요청합니다.
     * @param accessToken 네이버 액세스 토큰
     * @return 사용자 정보 응답 객체
     */
    public NaverUserInfoResponse getUserInfo(String accessToken) {
        String userInfoUrl = "https://openapi.naver.com/v1/nid/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        return restTemplate.postForObject(userInfoUrl, request, NaverUserInfoResponse.class);
    }
}
