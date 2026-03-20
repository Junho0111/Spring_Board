package com.board.domain.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

/**
 * JDBC Template을 사용하여 데이터베이스의 Member 테이블에 접근하는 구현체입니다.
 * DB의 제약 조건(CASCADE 등)을 활용하여 연관 데이터를 관리합니다.
 */
@Slf4j
@Repository
public class MemberRepositoryJdbc implements MemberRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    /**
     * 데이터소스를 주입받아 JdbcTemplate과 SimpleJdbcInsert를 초기화합니다.
     * 주입되는 dataSource는 SpringBoot기본 설정인 HikariDataSource입니다.
     * @param dataSource 데이터베이스 커넥션 풀
     */
    public MemberRepositoryJdbc(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("member")
                .usingGeneratedKeyColumns("id");
    }

    /**
     * 새로운 회원을 데이터베이스에 저장합니다.
     * DB의 AUTO_INCREMENT 전략을 사용하여 생성된 ID를 객체에 다시 할당합니다.
     * @param member 저장할 회원 객체 (ID는 DB에서 생성됨)
     * @return 저장된 회원 객체 (생성된 ID 포함)
     */
    @Override
    public Member save(Member member) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("login_id", member.getLoginId())
                .addValue("name", member.getName())
                .addValue("password", member.getPassword());

        Number key = insertActor.executeAndReturnKey(params);
        member.setId(key.longValue());

        log.info("DB SAVE [ID={}, LoginID={}]", member.getId(), member.getLoginId());
        return member;
    }

    /**
     * 고유 식별자(ID)를 기준으로 하나의 회원 정보를 조회합니다.
     * @param id 조회할 회원의 고유 ID
     * @return 조회된 회원 객체 (존재하지 않을 경우 null 반환)
     */
    @Override
    public Member findById(Long id) {
        String sql = "SELECT * FROM member WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, memberRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("MEMBER NOT FOUND [ID={}]", id);
            return null;
        }
    }

    /**
     * 사용자의 로그인 ID를 기준으로 회원 정보를 조회합니다.
     * @param loginId 조회할 로그인용 아이디
     * @return Optional 형태의 회원 객체 (부재 시 빈 Optional 반환)
     */
    @Override
    public Optional<Member> findByLoginId(String loginId) {
        String sql = "SELECT * FROM member WHERE login_id = ?";
        List<Member> result = jdbcTemplate.query(sql, memberRowMapper(), loginId);
        return result.stream().findFirst();
    }

    /**
     * 데이터베이스에 저장된 모든 회원 목록을 조회합니다.
     * @return 전체 회원 리스트
     */
    @Override
    public List<Member> findAll() {
        String sql = "SELECT * FROM member";
        return jdbcTemplate.query(sql, memberRowMapper());
    }

    /**
     * 특정 회원의 이름과 비밀번호를 수정합니다.
     * @param memberId 수정할 회원의 고유 ID
     * @param newName 변경할 새 이름
     * @param newPassword 변경할 새 비밀번호
     * @throws IllegalArgumentException 해당 ID의 회원이 존재하지 않을 경우 발생
     */
    @Override
    public void update(Long memberId, String newName, String newPassword) {
        String sql = "UPDATE member SET name = ?, password = ? WHERE id = ?";
        int updateRow = jdbcTemplate.update(sql, newName, newPassword, memberId);

        if (updateRow == 0) {
            log.error("UPDATE FAILED: ID {} NOT FOUND", memberId);
            throw new IllegalArgumentException("수정 실패: 해당 ID의 회원이 존재하지 않습니다.");
        }
    }

    /**
     * 특정 회원을 삭제(탈퇴) 처리합니다.
     * DB의 ON DELETE CASCADE 설정으로 인해 해당 회원이 작성한
     * 게시물(post), 댓글(comment), 파일(upload_file)이 연쇄적으로 삭제됩니다.
     * @param id 삭제할 회원의 고유 ID
     * @return 삭제되기 전의 회원 객체 정보
     * @throws IllegalArgumentException 해당 ID의 회원이 존재하지 않을 경우 발생
     */
    @Override
    public Member delete(Long id) {
        Member member = findById(id);
        if (member == null) {
            log.error("DELETE FAILED: ID {} NOT FOUND", id);
            throw new IllegalArgumentException("삭제 실패: 해당 ID의 회원이 존재하지 않습니다.");
        }

        String sql = "DELETE FROM member WHERE id = ?";
        jdbcTemplate.update(sql, id);

        log.info("DB DELETED [ID={}] - 연관된 게시물/댓글/파일 자동 삭제됨", id);
        return member;
    }

    /**
     * 데이터베이스 결과셋(ResultSet)을 Member 도메인 객체로 변환하는 매퍼를 생성합니다.
     * snake_case로 된 DB 컬럼명을 camelCase로 된 객체 필드명에 자동으로 매핑합니다.
     * @return RowMapper 객체
     */
    private RowMapper<Member> memberRowMapper() {
        return BeanPropertyRowMapper.newInstance(Member.class);
    }
}