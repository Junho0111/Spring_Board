package com.board.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

/**
 * 모든 HTTP 요청에 대해 로그를 기록하는 인터셉터.
 * 요청 시작 시 고유 ID를 부여하여 요청의 전 생애주기를 추적할 수 있도록 지원합니다.
 */
@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    // 요청 고유 ID를 HTTP 요청 속성에 저장할 때 사용되는 키
    public static final String LOGIN_ID = "logId";

    /**
     * 컨트롤러 실행 전 호출됩니다.
     * 요청에 고유한 ID를 부여하고, 요청 정보를 로깅합니다.
     *
     * @param request 현재 HTTP 요청 객체
     * @param response 현재 HTTP 응답 객체
     * @param handler 실행될 핸들러 (컨트롤러 메서드 등)
     * @return 다음 인터셉터 또는 컨트롤러로 요청을 계속 진행할지 여부 (true: 진행, false: 중단)
     * @throws Exception 예외 발생 시
     */
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        String uuid = UUID.randomUUID().toString();
        request.setAttribute(LOGIN_ID, uuid);

        //@RequestMapping: HandlerMethod
        //정적 리소스: ResourceHttpRequestHandler
         if (handler instanceof HandlerMethod) {
             HandlerMethod hm = (HandlerMethod) handler;
         }

        log.info("REQUEST [{}][{}][{}]", uuid, requestURI, handler);
        return true;
    }

    /**
     * 컨트롤러 실행 후 뷰 렌더링 전 호출됩니다.
     * 모델앤뷰 정보를 로깅합니다.
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String logId = (String) request.getAttribute(LOGIN_ID);
        log.info("POST HANDLE [{}][{}][{}]", logId, handler, modelAndView);
    }

    /**
     * 뷰 렌더링 후 (또는 예외 발생 후) 최종적으로 호출됩니다.
     * 요청 처리 완료 정보를 로깅하고, 예외 발생 시 에러를 로깅합니다.
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String logId = (String) request.getAttribute(LOGIN_ID);

        log.info("RESPONSE [{}][{}][{}]", logId, requestURI, handler);
        if (ex != null) {
            log.error("AFTER COMPLETION error [{}][{}][{}]", logId, requestURI, ex);
        }
    }

}
