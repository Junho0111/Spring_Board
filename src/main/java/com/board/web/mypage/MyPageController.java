package com.board.web.mypage;

import com.board.domain.login.Kakao.KakaoService;
import com.board.domain.login.Naver.NaverService;
import com.board.domain.member.Member;
import com.board.domain.member.MemberRepository;
import com.board.domain.member.memberService.MemberService;
import com.board.domain.post.Post;
import com.board.domain.post.PostRepository;
import com.board.web.mypage.form.MemberEditForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/posts/my-page")
public class MyPageController {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final KakaoService kakaoService;
    private final NaverService naverService;

    @GetMapping
    public String myPageHome(@SessionAttribute("loginMember") Member loginMember, Model model) {
        model.addAttribute("loginMember", loginMember);

        return "mypage/myPageHome";
    }

    @GetMapping("/my-posts")
    public String myPostsList(@SessionAttribute("loginMember") Member loginMember, Model model) {
        List<Post> posts = postRepository.findByMemberId(loginMember.getId());
        model.addAttribute("posts", posts);
        model.addAttribute("loginMember", loginMember);

        return "mypage/myPostsList";
    }

    @GetMapping("/my-edit")
    public String myEditPageForm(@SessionAttribute("loginMember") Member loginMember, Model model) {
        model.addAttribute("memberEditForm", new MemberEditForm());
        return "mypage/myEditForm";
    }

    @PostMapping("/my-edit")
    public String edit(HttpServletRequest request, @Validated @ModelAttribute("memberEditForm") MemberEditForm form, BindingResult bindingResult, @SessionAttribute("loginMember") Member loginMember, RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()) {
            return "mypage/myEditForm";
        }

        String newPassword = form.getNewPassword();
        String newName = form.getNewName();

        memberService.updateAuthorNameInPostsAndComments(loginMember.getId(), newName);
        memberRepository.update(loginMember.getId(), newName, newPassword);

        Member updateLoginMember = memberService.findMemberById(loginMember.getId());

        HttpSession session = request.getSession(false);
        session.setAttribute("loginMember", updateLoginMember);

        redirectAttributes.addFlashAttribute("successMessage", "성공적으로 수정 되었습니다.");
        return "redirect:/posts/my-page";
    }

    /**
     * 회원 탈퇴 요청을 처리합니다.
     * 카카오로 가입한 회원의 경우, 카카오 서버와의 연동을 해제(Unlink)한 후 로컬 DB에서 회원 정보를 삭제합니다.
     * @param loginMember 현재 로그인된 회원 정보
     * @param request HTTP 요청 객체 (세션 무효화용)
     * @return 홈 화면으로 리다이렉트
     */
    @PostMapping("/delete")
    public String delete(@SessionAttribute("loginMember") Member loginMember, HttpServletRequest request) {
        if (loginMember.getKakaoId() != null) {
            try {
                kakaoService.unlink(loginMember.getKakaoId());
            } catch (Exception e) {
                log.error("Kakao unlink failed but proceeding with account deletion", e);
            }
        }

        if (loginMember.getNaverId() != null) {
            // 네이버 연동 해제는 일반적으로 액세스 토큰이 필요하므로 여기서는 로그만 남김
            // 추가로 네이버 로그인 가입 url이 auth_type=reauthenticate 이므로 매번 검사함(돌려막기지만 결과은 같음)
            log.info("Naver account deletion: [naverId={}]", loginMember.getNaverId());
        }

        memberService.deleteMember(loginMember.getId());

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return "redirect:/";
    }

}