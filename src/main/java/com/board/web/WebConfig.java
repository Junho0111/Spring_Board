package com.board.web;

import com.board.web.interceptor.LogInterceptor;
import com.board.web.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 설정을 담당하는 Configuration 클래스.
 * 인터셉터를 등록하고 관리하는 역할을 수행합니다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 인터셉터를 등록하는 메서드.
     * 다양한 인터셉터를 체인으로 구성하여 요청 처리 전후에 공통 로직을 적용할 수 있습니다.
     *
     * @param registry 인터셉터 등록을 위한 InterceptorRegistry 객체
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error"); // 정적 리소스 및 에러 페이지 제외

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/members/add", "/login", "/login/**", "/logout", "/css/**", "/*.ico", "/error"); // 홈, 회원가입, 로그인 관련, 정적 리소스, 에러 페이지 제외
    }
}
