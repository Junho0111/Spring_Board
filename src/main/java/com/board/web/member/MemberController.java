package com.board.web.member;

import com.board.domain.member.Member;
import com.board.domain.member.memberService.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 회원 관련 웹 요청을 처리하는 컨트롤러입니다.
 */
@Slf4j
@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원 가입 폼을 보여줍니다.
     * @param member 모델에 바인딩될 비어있는 Member 객체
     * @return 회원 가입 폼 뷰의 논리적 이름
     */
    @GetMapping("/add")
    public String addForm(@ModelAttribute("member") Member member) {
        return "members/addMemberForm";
    }

    /**
     * 신규 회원을 등록합니다.
     * 입력된 회원 정보에 대한 유효성 검사를 수행하며, 중복 아이디가 있을 경우 오류를 처리합니다.
     * @param member     폼에서 제출된 회원 정보
     * @param bindingResult 유효성 검사 결과
     * @return 성공 시 리다이렉트 URL, 실패 시 회원 가입 폼 뷰
     */
    @PostMapping("/add")
    public String save(@Validated @ModelAttribute("member") Member member, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            return "members/addMemberForm";
        }

        try {
            memberService.join(member);
        } catch (IllegalStateException e) {
            bindingResult.reject("saveFail", e.getMessage());
            return "members/addMemberForm";
        }

        return "redirect:/";
    }
}
