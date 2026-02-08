package com.board.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 사용자 로그인 상태를 확인하는 인터셉터.
 * 로그인되지 않은 사용자의 요청을 로그인 페이지로 리다이렉트합니다.
 */
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    /**
     * 컨트롤러 실행 전 호출되어 로그인 상태를 확인합니다.
     *
     * @param request 현재 HTTP 요청
     * @param response 현재 HTTP 응답
     * @param handler 실행될 핸들러(컨트롤러 메서드)
     * @return 사용자가 로그인되어 있으면 true, 아니면 로그인 페이지로 리다이렉트 후 false
     * @throws Exception 예외 발생 시
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        log.info("Login Check Interceptor 실행 [요청 경로={}]", requestURI);

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loginMember") == null) {
            log.info("미인증 사용자 요청: {}", requestURI);

            // 로그인 페이지로 리다이렉트 및 요청했던 URI를 로그인 후 원래 페이지로 이동하기 위해 쿼리 파라미터로 전달
            response.sendRedirect("/login?redirectURL=" + requestURI);
            return false;
        }

        log.info("인증된 사용자 요청: {}", requestURI);
        return true;
    }
}
