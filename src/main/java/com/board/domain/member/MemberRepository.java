package com.board.domain.member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    public Member save(Member member);

    public Member findById(Long id);

    public Optional<Member> findByLoginId(String loginId);

    public List<Member> findAll();
}
