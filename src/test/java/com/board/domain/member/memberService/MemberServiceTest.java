package com.board.domain.member.memberService;

import com.board.domain.member.Member;
import com.board.domain.member.MemberRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MemberServiceTest {

    MemberService memberService;
    MemberRepositoryImpl memberRepository;

    @BeforeEach
    public void beforeEach() {
        memberRepository = new MemberRepositoryImpl();
        memberService = new MemberService(memberRepository);
    }

    @AfterEach
    void afterEach() {
        memberRepository.clearStore();
    }

    @Test
    void 회원가입() {
        //given
        Member member = new Member("test", "테스터", "test");

        //when
        Member joinResult = memberService.join(member);

        //then
        assertThat(joinResult.getLoginId()).isEqualTo(member.getLoginId());
    }

    @Test
    void 중복_회원_예외() {
        //given
        Member member = new Member("test", "테스터", "test");
        Member member2 = new Member("test", "테스터1", "test1");

        //when
        memberService.join(member);

        //then
        try {
            memberService.join(member2);
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("이미 존재하는 아이디입니다.");
        }
    }
}