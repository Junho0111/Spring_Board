package com.board.web.mypage;

import com.board.domain.member.Member;
import com.board.domain.member.MemberRepository;
import com.board.domain.post.Post;
import com.board.domain.post.PostRepository;
import com.board.web.mypage.form.MemberEditForm;
import com.board.web.post.form.PostForm;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.http.HttpRequest;
import java.util.List;


@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/posts/my-page")
public class MyPageController {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

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
    public String edit(@Validated @ModelAttribute("memberEditForm") MemberEditForm form, BindingResult bindingResult, @SessionAttribute("loginMember") Member loginMember, RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()) {
            return "mypage/myEditForm";
        }

        String newPassword = form.getNewPassword();
        String newName = form.getNewName();

        memberRepository.update(loginMember.getId(), newName, newPassword);
        return "redirect:/posts/my-page";
    }

}