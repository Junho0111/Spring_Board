package com.board.domain.login.Kakao;

import com.board.domain.login.Kakao.Dto.KakaoTokenResponse;
import com.board.domain.login.Kakao.Dto.KakaoUserInfoResponse;
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
 * 카카오 OAuth2 API와 통신을 담당하는 서비스 클래스입니다.
 * 액세스 토큰 발급, 사용자 정보 조회, 연결 끊기(Unlink) 기능을 제공합니다.
 */
@Slf4j
@Service
public class KakaoService {

    /** 카카오 REST API 키 */
    @Value("${kakao.client.id}")
    private String clientId;

    /** 카카오 Redirect URI */
    @Value("${kakao.redirect.uri}")
    private String redirectUri;

    /** 카카오 어드민 키 (연결 끊기 등에 사용) */
    @Value("${kakao.admin.key}")
    private String adminKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 카카오로부터 받은 인가 코드를 사용하여 액세스 토큰을 요청합니다.
     * @param code 카카오 인증 서버에서 발급한 인가 코드
     * @return 발급된 액세스 토큰 문자열
     */
    public String getAccessToken(String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        KakaoTokenResponse response = restTemplate.postForObject(tokenUrl, request, KakaoTokenResponse.class);

        if (response != null) {
            return response.accessToken();
        }
        return null;
    }

    /**
     * 액세스 토큰을 사용하여 카카오 사용자 정보를 요청합니다.
     * @param accessToken 카카오 액세스 토큰
     * @return 사용자 정보 응답 객체
     */
    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        return restTemplate.postForObject(userInfoUrl, request, KakaoUserInfoResponse.class);
    }

    /**
     * 카카오 연결 끊기 (회원 탈퇴 시 호출)
     * 관리자 키(Admin Key)를 사용하여 카카오 서버에 해당 사용자와의 앱 연결 해제를 요청합니다.
     * @param kakaoId 연결을 끊을 사용자의 카카오 고유 ID
     */
    public void unlink(String kakaoId) {
        String unlinkUrl = "https://kapi.kakao.com/v1/user/unlink";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + adminKey); // Admin Key 사용
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("target_id_type", "user_id");
        params.add("target_id", kakaoId);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        restTemplate.postForObject(unlinkUrl, request, String.class);
        log.info("Kakao Unlink SUCCESS [kakaoId={}]", kakaoId);
    }
}
