package com.board.domain.uploadfile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

/**
 * JDBC Template을 사용하여 데이터베이스의 upload_file 테이블에 접근하는 구현체입니다.
 */
@Slf4j
//@Repository
public class UploadFileRepositoryJdbc implements UploadFileRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    public UploadFileRepositoryJdbc(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("upload_file")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public void save(UploadFile file) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("post_id", file.getPostId())
                .addValue("store_file_name", file.getStoreFileName())
                .addValue("upload_file_name", file.getUploadFileName())
                .addValue("file_type", file.getFileType().name());

        Number key = insertActor.executeAndReturnKey(params);
        file.setId(key.longValue());

        log.info("JDBC FILE SAVE [ID={}, PostID={}, Type={}]", file.getId(), file.getPostId(), file.getFileType());
    }

    @Override
    public void deleteByPostId(Long postId) {
        String sql = "delete from upload_file where post_id = :postId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("postId", postId);
        jdbcTemplate.update(sql, params);
        log.info("JDBC FILES DELETED [PostID={}]", postId);
    }

    @Override
    public List<UploadFile> findAllByPostId(Long postId) {
        String sql = "select * from upload_file where post_id = :postId";
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("postId", postId);
        return jdbcTemplate.query(sql, params, fileRowMapper());
    }

    private RowMapper<UploadFile> fileRowMapper() {
        return BeanPropertyRowMapper.newInstance(UploadFile.class);
    }
}
