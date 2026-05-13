package com.board.domain.member;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis 매퍼 인터페이스입니다.
 * memberMapper.xml에 정의된 SQL 쿼리와 연결됩니다.
 */
@Mapper
public interface MemberRepositoryMybatis {

    /**
     * 회원을 저장합니다.
     * @param member 저장할 회원 객체
     */
    void save(Member member);

    /**
     * ID로 회원을 조회합니다.
     * @param id 조회할 회원의 고유 ID
     * @return 조회된 회원 객체
     */
    Member findById(Long id);

    /**
     * 로그인 ID로 회원을 조회합니다.
     * @param loginId 조회할 로그인 ID
     * @return 조회된 회원 객체 (Optional)
     */
    Optional<Member> findByLoginId(String loginId);

    /**
     * 카카오 ID를 통한 회원 단건 조회
     * @param kakaoId 조회할 카카오 고유 ID
     * @return 조회된 회원 객체 (Optional)
     */
    Optional<Member> findByKakaoId(@Param("kakaoId") String kakaoId);

    /**
     * 모든 회원을 조회합니다.
     * @return 회원 리스트
     */
    List<Member> findAll();

    /**
     * 회원 정보를 수정합니다.
     * @param memberId 수정할 회원의 ID
     * @param name 변경할 이름
     * @param password 변경할 비밀번호
     */
    void update(@Param("id") Long memberId, @Param("name") String name, @Param("password") String password);

    /**
     * 회원을 삭제합니다.
     * @param id 삭제할 회원의 ID
     */
    void delete(Long id);
}
