package com.board.web.login;

import com.board.domain.login.LoginService;
import com.board.domain.member.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 로그인 요청을 처리하는 컨트롤러.
 * 사용자의 로그인 및 로그아웃 기능을 제공합니다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    /** 로그인 서비스 의존성 주입 */
    private final LoginService loginService;

    /**
     * 로그인 폼을 보여줍니다.
     * @param loginForm 로그인 폼 데이터를 바인딩할 객체
     * @return 로그인 폼 뷰 경로
     */
    @GetMapping("/login")
    public String login(@ModelAttribute("loginForm") LoginForm loginForm ) {
        return "login/loginForm";
    }

    /**
     * 로그인 요청을 처리합니다.
     * 아이디와 비밀번호를 검증하고, 유효한 경우 로그인 처리 후 세션에 회원 정보를 저장하고 메인 페이지로 리다이렉트합니다.
     * @param loginForm 로그인 폼 데이터
     * @param bindingResult 유효성 검사 결과
     * @param request HTTP 서블릿 요청 객체
     * @return 로그인 성공 시 메인 페이지로 리다이렉트, 실패 시 로그인 폼으로 돌아감
     */
    @PostMapping("/login")
    public String login(@Validated @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        log.info("login: {}", loginMember);

        // 로그인 성공 처리
        // 세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성
        HttpSession session = request.getSession();
        // 세션에 로그인 회원 정보 보관
        session.setAttribute("loginMember", loginMember);

        return "redirect:/";
    }

    /**
     * 로그아웃 요청을 처리합니다.
     * 현재 세션을 무효화하여 로그아웃 처리하고 메인 페이지로 리다이렉트합니다.
     * @param request HTTP 서블릿 요청 객체
     * @return 메인 페이지로 리다이렉트
     */
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        // 세션을 삭제한다.
        HttpSession session = request.getSession(false);

        if (session != null) {
            Member logoutMember = (Member) session.getAttribute("loginMember");
            session.invalidate();
            log.info("logout: {}", logoutMember);
        }

        return "redirect:/";
    }
}
