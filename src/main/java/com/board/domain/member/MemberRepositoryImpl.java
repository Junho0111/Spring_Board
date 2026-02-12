package com.board.domain.member;

import com.board.domain.comment.Comment;
import com.board.domain.post.Post;
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
    private static final Map<Long,Member> store = new HashMap<>();

    /** 회원 ID 생성을 위한 시퀀스입니다. */
    private static long sequence = 0L;

    @Override
    public Member save(Member member) {
        member.setId(++sequence);
        log.info("SAVE [ID={}, LoginID={}, Name={}, Passward={}]", member.getId(), member.getLoginId(), member.getName(), member.getPassword());
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

    @Override
    public void update(Long memberId, String newName, String newPassword) {
        Member findMember = findById(memberId);

        if (findMember == null) {
            log.error("UPDATE FAILED: ID {} NOT FOUND", memberId);
            throw new IllegalArgumentException("수정 실패: 해당 ID(" + memberId + ")의 회원이 존재하지 않습니다.");
        }

        findMember.setName(newName);
        findMember.setPassword(newPassword);
        log.info("UPDATED [ID={}, LoginID={}, Name={}, Password={}]", findMember.getId(), findMember.getLoginId(), findMember.getName(), findMember.getPassword());
    }

    @Override
    public Member delete(Long id) {
        Member deleteMember = findById(id);

        if (deleteMember == null) {
            log.error("DELETE FAILED: ID {} NOT FOUND", id);
            throw new IllegalArgumentException("삭제 실패: 해당 ID(" + id + ")의 회원이 존재하지 않습니다.");
        }

        store.remove(id);
        log.info("DELETED [ID={}, loginId={}, name={}, password={}]", id, deleteMember.getLoginId(), deleteMember.getName(), deleteMember.getPassword());
        return deleteMember;
    }

    public void clearStore() {
        store.clear();
    }
}
