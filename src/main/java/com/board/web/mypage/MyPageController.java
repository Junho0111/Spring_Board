package com.board.web.mypage;

import com.board.domain.member.Member;
import com.board.domain.post.Post;
import com.board.domain.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;


@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/posts/my-page")
public class MyPageController {

    private final PostRepository postRepository;

    @GetMapping
    public String myPageHome(@SessionAttribute("loginMember") Member loginMember, Model model) {
        model.addAttribute("loginMember", loginMember);

        return "my-page/myPageHome";
    }

    @GetMapping("/my-posts")
    public String myPostsList(@SessionAttribute("loginMember") Member loginMember, Model model) {
        List<Post> posts = postRepository.findByMemberId(loginMember.getId());
        model.addAttribute("posts", posts);
        model.addAttribute("loginMember", loginMember);

        return "my-page/myPostsList";
    }
}