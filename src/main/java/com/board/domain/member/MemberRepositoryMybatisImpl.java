package com.board.domain.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis를 사용하여 데이터베이스의 Member 테이블에 접근하는 구현체입니다.
 * {@link MemberRepository} 인터페이스를 구현하며, 실제 쿼리는 {@link MemberRepositoryMybatis} 매퍼에 위임합니다.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRepositoryMybatisImpl implements MemberRepository {

    private final MemberRepositoryMybatis memberMapper;

    /**
     * 새로운 회원을 데이터베이스에 저장합니다.
     * MyBatis 매퍼를 통해 저장을 실행하며, 생성된 고유 ID가 객체에 할당됩니다.
     * @param member 저장할 회원 객체
     * @return 저장된 회원 객체 (생성된 ID 포함)
     */
    @Override
    public Member save(Member member) {
        memberMapper.save(member);
        log.info("MyBatis SAVE [ID={}, LoginID={}]", member.getId(), member.getLoginId());
        return member;
    }

    /**
     * 고유 식별자(ID)를 기준으로 하나의 회원 정보를 조회합니다.
     * @param id 조회할 회원의 고유 ID
     * @return 조회된 회원 객체 (존재하지 않을 경우 null 반환)
     */
    @Override
    public Member findById(Long id) {
        return memberMapper.findById(id);
    }

    /**
     * 사용자의 로그인 ID를 기준으로 회원 정보를 조회합니다.
     * @param loginId 조회할 로그인용 아이디
     * @return Optional 형태의 회원 객체 (부재 시 빈 Optional 반환)
     */
    @Override
    public Optional<Member> findByLoginId(String loginId) {
        return memberMapper.findByLoginId(loginId);
    }

    /**
     * 카카오 고유 식별자(kakaoId)를 기준으로 회원 정보를 조회합니다.
     * @param kakaoId 조회할 카카오 고유 ID
     * @return Optional 형태의 회원 객체 (부재 시 빈 Optional 반환)
     */
    @Override
    public Optional<Member> findByKakaoId(String kakaoId) {
        return memberMapper.findByKakaoId(kakaoId);
    }

    /**
     * 데이터베이스에 저장된 모든 회원 목록을 조회합니다.
     * @return 전체 회원 리스트
     */
    @Override
    public List<Member> findAll() {
        return memberMapper.findAll();
    }

    /**
     * 특정 회원의 이름과 비밀번호를 수정합니다.
     * @param memberId 수정할 회원의 고유 ID
     * @param newName 변경할 새 이름
     * @param newPassword 변경할 새 비밀번호
     * @throws IllegalArgumentException 해당 ID의 회원이 존재하지 않을 경우 발생 (MyBatis 결과 확인 로직은 생략됨)
     */
    @Override
    public void update(Long memberId, String newName, String newPassword) {
        memberMapper.update(memberId, newName, newPassword);
        log.info("MyBatis UPDATE [ID={}, Name={}]", memberId, newName);
    }

    /**
     * 특정 회원을 삭제(탈퇴) 처리합니다.
     * DB의 ON DELETE CASCADE 설정으로 인해 해당 회원이 작성한
     * 게시물(post), 댓글(comment), 파일(upload_file)이 연쇄적으로 삭제됩니다.
     * @param id 삭제할 회원의 고유 ID
     * @return 삭제되기 전의 회원 객체 정보
     */
    @Override
    public Member delete(Long id) {
        Member member = findById(id);
        if (member != null) {
            memberMapper.delete(id);
            log.info("MyBatis DELETED [ID={}]", id);
        }
        return member;
    }
}
