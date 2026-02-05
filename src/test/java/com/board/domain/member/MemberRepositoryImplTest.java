package com.board.domain.member;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class MemberRepositoryImplTest {

    MemberRepositoryImpl memberRepository = new MemberRepositoryImpl();

    @AfterEach
    void afterEach() {
        memberRepository.clearStore();
    }

    @Test
    void save() {
        //given
        Member member = new Member("test", "테스터", "test");

        //when
        Member saveMember = memberRepository.save(member);

        //then
        assertThat(saveMember).isEqualTo(member);
    }

    @Test
    void findById() {
        //given
        Member member = new Member("test", "테스터", "test");

        //when
        Member saveMember = memberRepository.save(member);

        //then
        Member findMember = memberRepository.findById(member.getId());
        assertThat(findMember).isEqualTo(saveMember);
    }

    @Test
    void findByLoginId() {
        //given
        Member member1 = new Member("test", "테스터", "test");

        //when
        memberRepository.save(member1);

        //then
        Optional<Member> notNullResult = memberRepository.findByLoginId(member1.getLoginId());
        assertThat(notNullResult).contains(member1);
        assertThat(notNullResult.get()).isEqualTo(member1);

        Optional<Member> nullResult = memberRepository.findByLoginId("없는LoginId");
        assertThat(nullResult).isEmpty();
    }

    @Test
    void findAll() {
        //given
        Member member1 = new Member("test", "테스터", "test");
        Member member2 = new Member("test2", "테스터2", "test2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        List<Member> members = memberRepository.findAll();

        //then
        assertThat(members.size()).isEqualTo(2);
        assertThat(members).contains(member1, member2);
    }
}