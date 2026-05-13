package com.board.web.login;

import com.board.domain.login.Kakao.KakaoService;
import com.board.domain.login.Kakao.Dto.KakaoUserInfoResponse;
import com.board.domain.member.Member;
import com.board.domain.member.memberService.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

import static java.lang.String.valueOf;

/**
 * OAuth2 인증 콜백 및 연동 처리를 담당하는 컨트롤러입니다.
 * 카카오 로그인 인증 후의 후속 처리(회원 가입, 세션 생성 등)를 수행합니다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class OAuthController {

    private final KakaoService kakaoService;
    private final MemberService memberService;

    /**
     * 카카오 로그인 인증 완료 후 호출되는 콜백 메서드입니다.
     * state 파라미터를 통해 사용자의 의도(가입 또는 로그인)를 구분하여 처리합니다.
     * @param code 카카오 인증 서버에서 전달한 인가 코드
     * @param state 사용자의 요청 의도 (signup: 가입, login: 로그인)
     * @param request HTTP 요청 객체 (세션 생성용)
     * @return 의도 및 가입 여부에 따른 다음 리다이렉트 주소
     */
    @GetMapping("/oauth/kakao/callback")
    public String kakaoCallback(@RequestParam String code, @RequestParam(required = false) String state, HttpServletRequest request) {
        log.info("Kakao callback code: {}, state: {}", code, state);

        String accessToken = kakaoService.getAccessToken(code);
        if (accessToken == null) {
            return "redirect:/login?error";
        }

        KakaoUserInfoResponse userInfo = kakaoService.getUserInfo(accessToken);
        String kakaoId = valueOf(userInfo.id());

        Optional<Member> findMember = memberService.findMemberByKakaoId(kakaoId);

        // [회원가입 모드]
        if ("signup".equals(state)) {
            if (findMember.isPresent()) {
                log.info("[Already registered Kakao user] Redirecting to signup form.");
                return "redirect:/members/add?alreadyRegistered";
            }

            return "redirect:/members/add/kakao?kakaoId=" + kakaoId;
        }

        // [로그인 모드] 이미 계정이 존재하는 경우 즉시 로그인 처리
        if (findMember.isPresent()) {
            Member member = findMember.get();
            HttpSession session = request.getSession();
            session.setAttribute("loginMember", member);
            log.info("Kakao Login SUCCESS: [Name={}]", member.getName());

            return "redirect:/";
        }

        return "redirect:/members/add?notRegistered";
    }

}


