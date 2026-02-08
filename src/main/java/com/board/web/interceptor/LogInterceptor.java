package com.board.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

/**
 * 요청의 시작부터 끝까지 로그를 남기는 인터셉터.
 * 각 요청에 고유한 ID를 부여하여 요청의 흐름을 추적합니다.
 */
@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    // 요청 고유 ID를 저장하는 데 사용될 속성 이름
    public static final String LOGIN_ID = "loginId";

    /**
     * 컨트롤러 실행 전 호출됩니다.
     * 요청에 고유한 ID를 부여하고, 요청 정보를 로깅합니다.
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
        log.info("postHandle [{}]", modelAndView);
    }

    /**
     * 뷰 렌더링 후 (또는 예외 발생 후) 최종적으로 호출됩니다.
     * 요청 처리 완료 정보를 로깅하고, 예외 발생 시 에러를 로깅합니다.
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String logId = (String) request.getAttribute(LOGIN_ID);

        log.info("RESPONSE [{}][{}]", logId, requestURI);
        if (ex != null) {
            log.error("afterCompletion error [{}][{}]", logId, ex.getMessage());
        }
    }

}
