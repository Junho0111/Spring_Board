package com.board.domain.login;

import com.board.domain.comment.CommentRepository;
import com.board.domain.member.Member;
import com.board.domain.member.MemberRepositoryImpl;
import com.board.domain.member.memberService.MemberService;
import com.board.domain.post.PostRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class LoginServiceTest {

    MemberService memberService;
    MemberRepositoryImpl memberRepository;
    LoginService loginService;
    CommentRepository commentRepository;
    PostRepository postRepository;

    @BeforeEach
    public void beforeEach() {
        memberRepository = new MemberRepositoryImpl();
        memberService = new MemberService(memberRepository, postRepository, commentRepository);
        loginService = new LoginService(memberRepository);
    }

    @AfterEach
    void afterEach() {
        memberRepository.clearStore();
    }
    @Test
    void 로그인_성공() {
        //given
        Member member = new Member("testID", "test", "testPW");
        memberService.join(member);

        //when
        Member loginResult = loginService.login("testID", "testPW");

        //then
        assertThat(loginResult).isNotNull();
        assertThat(loginResult.getLoginId()).isEqualTo(member.getLoginId());
        assertThat(loginResult.getPassword()).isEqualTo(member.getPassword());

    }

    @Test
    void 로그인_실패() {
        //given
        Member member = new Member("testID", "test", "testPW");
        memberService.join(member);

        //when
        Member loginResult = loginService.login("로그인실패", "로그인실패");

        //then
        assertThat(loginResult).isNull();
    }
}