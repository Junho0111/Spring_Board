package com.board.domain.uploadfile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Slf4j
@Repository
public class UploadFileRepositoryJdbc {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    public UploadFileRepositoryJdbc(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("upload_file")
                .usingGeneratedKeyColumns("id");
    }

    public UploadFile save(UploadFile file) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("post_id", file.getPostId())
                .addValue("store_file_name", file.getStoreFileName())
                .addValue("upload_file_name", file.getUploadFileName())

                // Enum을 DB에 저장할때 -> .name()을 써서 문자열로 변환
                .addValue("file_type", file.getFileType().name());

        Number key = insertActor.executeAndReturnKey(params);
        file.setId(key.longValue());

        log.info("FILE SAVE [ID={}, PostID={}, Type={}]", file.getId(), file.getPostId(), file.getFileType());
        return file;
    }

    public void deleteByPostId(Long postId) {
        String sql = "delete from upload_file where post_id = ?";
        jdbcTemplate.update(sql, postId);
        log.info("FILES DELETED [PostID={}]", postId);
    }
}