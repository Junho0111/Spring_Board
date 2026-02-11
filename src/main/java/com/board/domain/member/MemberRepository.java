package com.board.domain.member;

import java.util.List;
import java.util.Optional;

/**
 * 회원 데이터에 접근하기 위한 리포지토리 인터페이스입니다.
 */
public interface MemberRepository {

    /**
     * 회원을 저장소에 저장합니다.
     * @param member 저장할 회원 객체
     * @return 저장된 회원 객체
     */
    public Member save(Member member);

    /**
     * ID로 회원을 찾습니다.
     * @param id 찾을 회원의 ID
     * @return 찾아낸 회원 객체. 없으면 null을 반환할 수 있습니다.
     */
    public Member findById(Long id);

    /**
     * 로그인 ID로 회원을 찾습니다.
     * @param loginId 찾을 회원의 로그인 ID
     * @return 찾아낸 회원 객체를 담은 Optional. 없으면 빈 Optional을 반환합니다.
     */
    public Optional<Member> findByLoginId(String loginId);

    /**
     * 모든 회원을 찾아 리스트로 반환합니다.
     * @return 모든 회원 정보를 담은 리스트
     */
    public List<Member> findAll();

    /**
     * 지정된 ID의 회원을 업데이트합니다.
     * @param memberId 업데이트할 회원의 ID
     * @param newName 업데이트할 회원의 새로운 이름
     * @param newPassword 업데이트할 회원의 새로운 비밀번호
     */
    void update(Long memberId, String newName, String newPassword);
}
