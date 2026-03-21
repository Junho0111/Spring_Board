package com.board.domain.member;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * MemberRepositoryJdbc 테스트
 * @Transactional: 테스트 완료 후 데이터를 자동으로 Rollback 하여 DB를 깨끗하게 유지합니다.
 * @SpringBootTest: 실제 스프링 컨테이너를 띄워 DataSource, 컨테이너가 관리하는 빈, 리포지토리 등을 주입받습니다.
 */
@SpringBootTest
@Transactional
class MemberRepositoryJdbcTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    void 회원저장_및_ID로_조회() {
        // given
        Member member = new Member("test", "테스터", "test");

        // when
        Member savedMember = memberRepository.save(member);

        // then
        Member findMember = memberRepository.findById(savedMember.getId());
        assertThat(findMember).isNotNull();
        assertThat(findMember.getLoginId()).isEqualTo(member.getLoginId());
        assertThat(findMember.getName()).isEqualTo(member.getName());
    }

    @Test
    void 로그인ID로_회원_조회() {
        // given
        Member member = new Member("test", "테스터", "test");
        Member saveMember = memberRepository.save(member);

        // when
        Optional<Member> findMember = memberRepository.findByLoginId("test");

        // then
        assertThat(findMember).isPresent();
        assertThat(findMember.get()).isEqualTo(saveMember);
    }

    @Test
    void 모든회원_조회() {
        // given
        Member member = memberRepository.save(new Member("test", "테스터", "test"));
        Member member2 = memberRepository.save(new Member("test2", "테스터2", "test2"));

        // when
        List<Member> members = memberRepository.findAll();

        // then
        assertThat(members.size()).isEqualTo(2);
        assertThat(members).contains(member, member2);
    }

    @Test
    void 회원정보_수정() {
        // given
        Member member = memberRepository.save(new Member("test", "테스터", "test"));
        Long memberId = member.getId();

        // when
        memberRepository.update(memberId, "new테스터", "newtest");

        // then
        Member updatedMember = memberRepository.findById(memberId);
        assertThat(updatedMember.getName()).isEqualTo("new테스터");
        assertThat(updatedMember.getPassword()).isEqualTo("newtest");
    }

    @Test
    void 회원삭제() {
        // given
        Member member = memberRepository.save(new Member("test", "테스터", "test"));
        Long memberId = member.getId();

        // when
        memberRepository.delete(memberId);

        // then
        Member findMember = memberRepository.findById(memberId);
        assertThat(findMember).isNull();
    }

    @Test
    void 존재하지않는_회원수정할때_예외() {
        assertThatThrownBy(() -> memberRepository.update(1L, "테스터", "test"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 존재하지않는_회원삭제할때_예외() {
        assertThatThrownBy(() -> memberRepository.delete(1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 존재하지않는_회원찾때_예외() {
        // given
        Long testId = 1L;

        // when
        Member findMember = memberRepository.findById(testId);

        // then
        assertThat(findMember).isNull();
    }
}

