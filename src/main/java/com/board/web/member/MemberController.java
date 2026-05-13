package com.board.web.member;

import com.board.domain.member.Member;
import com.board.domain.member.memberService.MemberService;
import com.board.web.member.Dto.KakaoJoinForm;
import com.board.web.member.Dto.NaverJoinForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import static java.util.UUID.randomUUID;

/**
 * 회원 관련 웹 요청을 처리하는 컨트롤러입니다.
 */
@Slf4j
@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /** 카카오 REST API 키 */
    @Value("${kakao.client.id}")
    private String kakaoClientId;

    /** 카카오 Redirect URI */
    @Value("${kakao.redirect.uri}")
    private String kakaoRedirectUri;

    /** 네이버 Client ID */
    @Value("${naver.client.id}")
    private String naverClientId;

    /** 네이버 Redirect URI */
    @Value("${naver.redirect.uri}")
    private String naverRedirectUri;

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

    /**
     * 카카오 전용 회원가입 폼을 보여줍니다.
     * 카카오 인증 후 전달받은 kakaoId를 폼 객체에 설정하여 뷰로 전달합니다.
     * @param kakaoId 카카오 고유 식별자
     * @param model 뷰에 데이터를 전달하기 위한 모델 객체
     * @return 카카오 회원가입 폼 뷰 이름
     */
    @GetMapping("/add/kakao")
    public String addKakaoForm(@RequestParam String kakaoId, Model model) {
        KakaoJoinForm form = new KakaoJoinForm();
        form.setKakaoId(kakaoId);
        model.addAttribute("kakaoJoinForm", form);

        return "members/addKakaoMemberForm";
    }

    /**
     * 카카오 정보를 기반으로 신규 회원을 등록합니다.
     * 사용자가 입력한 이름을 반영하여 가입을 완료하며, 가입 후 홈 화면으로 이동합니다.
     * @param form 카카오 가입 정보 (kakaoId, 사용자가 입력한 name, password)
     * @param bindingResult 유효성 검사 결과
     * @return 성공 시 홈 화면 리다이렉트, 실패 시 가입 폼 뷰
     */
    @PostMapping("/add/kakao")
    public String saveKakao(@Validated @ModelAttribute("kakaoJoinForm") KakaoJoinForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "members/addKakaoMemberForm";
        }

        String kakaoLoginId = "kakao_" + form.getKakaoId();

        Member member = new Member(kakaoLoginId, form.getName(), form.getPassword(), "kakao", form.getKakaoId());

        try {
            memberService.join(member);
        } catch (IllegalStateException e) {
            bindingResult.reject("saveFail", e.getMessage());
            return "members/addKakaoMemberForm";
        }

        return "redirect:/";
    }

    /**
     * 네이버 전용 회원가입 폼을 보여줍니다.
     * 네이버 인증 후 전달받은 naverId를 폼 객체에 설정하여 뷰로 전달합니다.
     * @param naverId 네이버 고유 식별자
     * @param model 뷰에 데이터를 전달하기 위한 모델 객체
     * @return 네이버 회원가입 폼 뷰 이름
     */
    @GetMapping("/add/naver")
    public String addNaverForm(@RequestParam String naverId, Model model) {
        NaverJoinForm form = new NaverJoinForm();
        form.setNaverId(naverId);
        model.addAttribute("naverJoinForm", form);

        return "members/addNaverMemberForm";
    }

    /**
     * 네이버 정보를 기반으로 신규 회원을 등록합니다.
     * 사용자가 입력한 이름을 반영하여 가입을 완료하며, 가입 후 홈 화면으로 이동합니다.
     * @param form 네이버 가입 정보 (naverId, 사용자가 입력한 name, password)
     * @param bindingResult 유효성 검사 결과
     * @return 성공 시 홈 화면 리다이렉트, 실패 시 가입 폼 뷰
     */
    @PostMapping("/add/naver")
    public String saveNaver(@Validated @ModelAttribute("naverJoinForm") NaverJoinForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "members/addNaverMemberForm";
        }

        String naverLoginId = "naver_" + form.getNaverId();

        Member member = new Member(naverLoginId, form.getName(), form.getPassword(), "naver", form.getNaverId());

        try {
            memberService.join(member);
        } catch (IllegalStateException e) {
            bindingResult.reject("saveFail", e.getMessage());
            return "members/addNaverMemberForm";
        }

        return "redirect:/";
    }

    /**
     * 회원 가입 폼을 보여줍니다.
     * 일반 가입 폼과 함께 카카오로 회원가입할 수 있는 링크를 제공합니다.
     * @param member 모델에 바인딩될 비어있는 Member 객체
     * @param model 뷰에 데이터를 전달하기 위한 모델 객체
     * @return 회원 가입 폼 뷰의 논리적 이름
     */
    @GetMapping("/add")
    public String addForm(@ModelAttribute("member") Member member, Model model) {
        // state=signup을 추가하여 가입 목적으로 카카오 인증을 요청함을 명시
        String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="
                + kakaoClientId + "&redirect_uri=" + kakaoRedirectUri + "&prompt=login&state=signup";
        model.addAttribute("kakaoLoginUrl", kakaoLoginUrl);

        // state=signup을 추가하여 가입 목적으로 네이버 인증을 요청함을 명시
        String naverLoginUrl = "https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id="
                + naverClientId + "&redirect_uri=" + naverRedirectUri + "&state=signup&auth_type=reauthenticate";
        model.addAttribute("naverLoginUrl", naverLoginUrl);

        return "members/addMemberForm";
    }

}
