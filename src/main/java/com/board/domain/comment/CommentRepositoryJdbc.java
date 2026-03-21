package com.board.domain.comment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC Template을 사용하여 데이터베이스의 Comment 테이블에 접근하는 구현체입니다.
 * 대댓글(계층형) 구조를 지원하며, SimpleJdbcInsert를 통해 데이터를 저장합니다.
 */
@Slf4j
@Repository
public class CommentRepositoryJdbc implements CommentRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    /**
     * 데이터소스를 주입받아 JdbcTemplate과 SimpleJdbcInsert를 초기화합니다.
     * @param dataSource 데이터베이스 커넥션 풀
     */
    public CommentRepositoryJdbc(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("comment")
                .usingGeneratedKeyColumns("id");
    }

    /**
     * 새로운 댓글을 데이터베이스에 저장합니다.
     * 생성 시각과 수정 시각을 현재 시간으로 설정하며, 생성된 고유 ID를 객체에 다시 할당합니다.
     * @param comment 저장할 댓글 객체
     * @return 저장된 댓글 객체 (DB에서 생성된 ID 및 시간 정보 포함)
     */
    @Override
    public Comment save(Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("post_id", comment.getPostId())
                .addValue("parent_comment_id", comment.getParentCommentId())
                .addValue("author", comment.getAuthor())
                .addValue("author_id", comment.getAuthorId())
                .addValue("content", comment.getContent())
                .addValue("created_at", comment.getCreatedAt())
                .addValue("updated_at", comment.getUpdatedAt());

        Number key = insertActor.executeAndReturnKey(params);
        comment.setId(key.longValue());

        log.info("DB SAVE [ID={}, Author={}]", comment.getId(), comment.getAuthor());
        return comment;
    }

    /**
     * 특정 댓글의 내용과 수정 시각을 업데이트합니다.
     * @param id 수정할 댓글의 고유 ID
     * @param content 변경할 새 내용
     * @throws IllegalArgumentException 해당 ID의 댓글이 존재하지 않을 경우 발생
     */
    @Override
    public void update(Long id, String content) {
        String sql = "UPDATE comment SET content = ?, updated_at = ? WHERE id = ?";
        int updated = jdbcTemplate.update(sql, content, LocalDateTime.now(), id);

        if (updated == 0) {
            log.error("UPDATE FAILED: ID {} NOT FOUND", id);
            throw new IllegalArgumentException("수정 실패: 해당 ID(" + id + ")의 댓글이 존재하지 않습니다.");
        }
    }

    /**
     * 특정 댓글의 작성자 이름을 업데이트합니다.
     * @param id 수정할 댓글의 고유 ID
     * @param author 변경할 새 작성자 이름
     */
    @Override
    public void updateAuthor(Long id, String author) {
        String sql = "UPDATE comment SET author = ? WHERE id = ?";
        jdbcTemplate.update(sql, author, id);
    }

    /**
     * 고유 식별자(ID)를 기준으로 하나의 댓글을 삭제합니다.
     * @param id 삭제할 댓글의 고유 ID
     * @return 삭제되기 전의 댓글 객체 정보
     */
    @Override
    public Comment delete(Long id) {
        Comment comment = findById(id);
        if (comment != null) {
            String sql = "DELETE FROM comment WHERE id = ?";
            jdbcTemplate.update(sql, id);
            log.info("DB DELETED [ID={}]", id);
        }
        return comment;
    }

    /**
     * 제공된 ID 리스트에 해당하는 모든 댓글을 순차적으로 삭제합니다.
     * @param commentIds 삭제할 댓글 ID 리스트
     */
    @Override
    public void deleteAllByIds(List<Long> commentIds) {
        if (commentIds.isEmpty()) {
            return;
        }
        for (Long commentId : commentIds) {
            delete(commentId);
        }
    }

    /**
     * 데이터베이스에 저장된 모든 댓글 목록을 조회합니다.
     * @return 전체 댓글 리스트
     */
    @Override
    public List<Comment> findAll() {
        String sql = "SELECT * FROM comment";
        return jdbcTemplate.query(sql, commentRowMapper());
    }

    /**
     * 고유 식별자(ID)를 기준으로 하나의 댓글 정보를 조회합니다.
     * @param id 조회할 댓글의 고유 ID
     * @return 조회된 댓글 객체 (존재하지 않을 경우 null 반환)
     */
    @Override
    public Comment findById(Long id) {
        String sql = "SELECT * FROM comment WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, commentRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("COMMENT NOT FOUND [ID={}]", id);
            return null;
        }
    }

    /**
     * 특정 게시물에 작성된 모든 댓글을 작성 순서대로 조회합니다.
     * @param postId 조회할 게시물의 고유 ID
     * @return 해당 게시물의 댓글 리스트
     */
    @Override
    public List<Comment> findAllByPostId(Long postId) {
        String sql = "SELECT * FROM comment WHERE post_id = ?";
        return jdbcTemplate.query(sql, commentRowMapper(), postId);
    }

    /**
     * 특정 게시물에 속한 모든 댓글을 일괄 삭제합니다.
     * @param postId 삭제할 댓글들이 속한 게시물의 ID
     */
    @Override
    public void deleteByPostId(Long postId) {
        String sql = "DELETE FROM comment WHERE post_id = ?";
        jdbcTemplate.update(sql, postId);
        log.info("ALL COMMENTS DELETED FOR POST [PostID={}]", postId);
    }

    /**
     * 특정 부모 댓글 하위의 모든 자식 및 자손 댓글 ID를 재귀적으로 조회합니다.
     * @param parentCommentId 최상위 부모가 되는 댓글 ID
     * @return 하위 자손 댓글들의 고유 ID 리스트
     */
    @Override
    public List<Long> findAllDescendantCommentIds(Long parentCommentId) {
        List<Long> descendantIds = new ArrayList<>();
        findDescendants(parentCommentId, descendantIds);
        return descendantIds;
    }

    /**
     * DB 조회를 통해 하위 댓글을 탐색하는 재귀 메서드입니다.
     * 깊이 우선 탐색(DFS) 방식으로 동작하며 각 단계에서 DB에 쿼리를 실행합니다.
     * @param parentId 현재 탐색의 기준이 되는 부모 ID
     * @param descendantIds 발견된 자손 ID들을 수집할 리스트
     */
    private void findDescendants(Long parentId, List<Long> descendantIds) {
        String sql = "SELECT id FROM comment WHERE parent_comment_id = ?";
        List<Long> directChildrenIds = jdbcTemplate.queryForList(sql, Long.class, parentId);

        for (Long childId : directChildrenIds) {
            descendantIds.add(childId);
            findDescendants(childId, descendantIds);
        }
    }

    /**
     * 데이터베이스 결과셋(ResultSet)을 Comment 도메인 객체로 변환하는 매퍼를 생성합니다.
     * snake_case로 된 DB 컬럼명을 camelCase로 된 객체 필드명에 자동으로 매핑합니다.
     * @return RowMapper 객체
     */
    private RowMapper<Comment> commentRowMapper() {
        return BeanPropertyRowMapper.newInstance(Comment.class);
    }
}