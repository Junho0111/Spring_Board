package com.board.domain.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * 회원 리포지토리의 메모리 내 구현체입니다.
 * {@link MemberRepository} 인터페이스를 구현합니다.
 */
@Slf4j
@Repository
public class MemberRepositoryImpl implements MemberRepository {

    /** 회원을 저장하기 위한 메모리 내 임시 저장소입니다. Key는 회원의 ID입니다. */
    private static Map<Long,Member> store = new HashMap<>();

    /** 회원 ID 생성을 위한 시퀀스입니다. */
    private static long sequence = 0L;

    @Override
    public Member save(Member member) {
        member.setId(++sequence);
        log.info("save: member={}", member);
        store.put(member.getId(), member);
        return member;
    }

    @Override
    public Member findById(Long id) {
        return store.get(id);
    }

    @Override
    public Optional<Member> findByLoginId(String loginId) {
        return findAll().stream()
                .filter(m -> m.getLoginId().equals(loginId))
                .findFirst();
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }
}
