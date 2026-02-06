package com.board;

import com.board.domain.member.Member;
import com.board.domain.member.memberService.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * 애플리케이션의 메인 페이지(홈) 요청을 처리하는 컨트롤러.
 * 로그인 상태에 따라 다른 뷰를 제공합니다.
 */
@Controller
public class HomeController {

    /**
     * 루트 경로 ("/") 요청을 처리합니다.
     * 세션에 로그인 회원 정보가 있는지 확인하여 로그인 여부에 따라 다른 홈 화면을 반환합니다.
     *
     * @param loginMember 세션에서 가져온 로그인 회원 객체 (로그인되어 있지 않으면 null)
     * @param model 뷰에 데이터를 전달하는 데 사용되는 모델 객체
     * @return 로그인하지 않은 경우 "home" 뷰, 로그인한 경우 "loginHome" 뷰
     */
    @GetMapping("/")
    public String homeLogin(@SessionAttribute(name = "loginMember", required = false) Member loginMember, Model model) {
        // 세션에 회원 데이터가 없으면 home으로 이동
        if(loginMember == null) {
            return "home";
        }

        // 세션이 유지되면 로그인 홈으로 이동
        model.addAttribute("loginMember", loginMember);
        return "loginHome";
    }
}
