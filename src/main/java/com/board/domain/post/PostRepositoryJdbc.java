package com.board.domain.post;

import com.board.domain.uploadfile.FileTypeEnum;
import com.board.domain.uploadfile.UploadFile;
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
import java.util.stream.Collectors;

/**
 * JDBC를 사용하여 데이터베이스에 게시물 정보를 저장하고 관리하는 리포지토리입니다.
 * {@link PostRepository} 인터페이스를 구현합니다.
 */
@Slf4j
@Repository
public class PostRepositoryJdbc implements PostRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    /**
     * 데이터소스를 주입받아 JdbcTemplate과 SimpleJdbcInsert를 초기화합니다.
     * @param dataSource 데이터베이스 커넥션 풀
     */
    public PostRepositoryJdbc(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("post")
                .usingGeneratedKeyColumns("id");
    }

    /**
     * 새로운 게시물을 데이터베이스에 저장하고, 생성된 고유 ID를 할당합니다.
     * 파일 정보는 별도의 테이블에 저장되므로 여기서는 본문 정보만 우선 처리합니다.
     *
     * @param post 저장할 게시물 객체
     * @return ID가 할당되어 저장된 게시물 객체
     */
    @Override
    public Post save(Post post) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", post.getTitle())
                .addValue("content", post.getContent())
                .addValue("author", post.getAuthor())
                .addValue("author_id", post.getAuthorId());

        Number key = insertActor.executeAndReturnKey(params);
        post.setId(key.longValue());

        log.info("DB SAVE [ID={}, Author={}, Title={}]", post.getId(), post.getAuthor(), post.getTitle());
        return post;
    }

    /**
     * 지정된 ID의 게시물 정보를 업데이트합니다.
     *
     * @param id          업데이트할 게시물의 ID
     * @param title       업데이트할 게시물 제목
     * @param content     업데이트할 게시물 내용
     */
    @Override
    public void update(Long id, String title, String content) {
        String sql = "update post set title = ?, content = ? where id = ?";
        int updateRow = jdbcTemplate.update(sql, title, content, id);

        if (updateRow == 0) {
            log.error("UPDATE FAILED: ID {} NOT FOUND", id);
            throw new IllegalArgumentException("수정 실패: 해당 ID(" + id + ")의 게시물이 존재하지 않습니다.");
        }
        log.info("UPDATED [ID={}, Title={}]", id, title);
    }

    /**
     * 지정된 ID의 게시물 작성자명을 업데이트합니다.
     *
     * @param id     업데이트할 게시물 ID
     * @param author 업데이트할 작성자 이름
     */
    @Override
    public void updateAuthor(Long id, String author) {
        String sql = "update post set author = ? where id = ?";
        jdbcTemplate.update(sql, author, id);
        log.info("AUTHOR UPDATED [ID={}, Author={}]", id, author);
    }

    /**
     * 지정된 ID의 게시물을 데이터베이스에서 삭제합니다.
     * DB의 ON DELETE CASCADE 설정에 의해 관련 파일 정보도 함께 삭제됩니다.
     *
     * @param id 삭제할 게시물의 ID
     * @return 삭제된 게시물 객체 (삭제 전 정보)
     */
    @Override
    public Post delete(Long id) {
        Post deletePost = findById(id);
        if (deletePost == null) {
            throw new IllegalArgumentException("삭제 실패: 해당 ID(" + id + ")의 게시물이 존재하지 않습니다.");
        }

        String sql = "delete from post where id = ?";
        jdbcTemplate.update(sql, id);

        log.info("DELETED [ID={}, Title={}]", id, deletePost.getTitle());
        return deletePost;
    }

    /**
     * 모든 게시물을 리스트 형태로 반환합니다.(업로드 파일 제외)
     *
     * @return 모든 게시물의 {@link List}
     */
    @Override
    public List<Post> findAll() {
        String sql = "select * from post";
        return jdbcTemplate.query(sql, postRowMapper());
    }

    /**
     * 검색 조건과 페이징 설정을 기준으로 게시물 목록을 조회합니다.
     *
     * @param type         조회할 타입 (author, title)
     * @param keyword      검색할 키워드
     * @param currentPage  현재 페이지 번호
     * @param postsPerPage 한 페이지에 보여줄 게시물 수
     * @return 검색 조건 및 페이징이 적용된 게시물 리스트
     */
    @Override
    public List<Post> postSearchFindAll(String type, String keyword, int currentPage, int postsPerPage) {
        int offset = (currentPage - 1) * postsPerPage;
        String sql = "select * from post where 1=1";
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            if ("author".equals(type)) {
                sql += " and author like ?";
                params.add("%" + keyword + "%");
            }

            if ("title".equals(type)) {
                sql += " and title like ?";
                params.add("%" + keyword + "%");
            }
        }

        sql += " order by id desc limit ? offset ?";
        params.add(postsPerPage);
        params.add(offset);

        return jdbcTemplate.query(sql, postRowMapper(), params.toArray());
    }

    /**
     * 검색 조건에 맞는 게시물의 총 개수를 반환합니다.
     *
     * @param type    조회할 타입
     * @param keyword 검색할 키워드
     * @return 검색 조건에 맞는 게시글 총 수
     */
    @Override
    public int postSearchCount(String type, String keyword) {
        String sql = "select count(*) from post where 1=1";
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            if ("author".equals(type)) {
                sql += " and author like ?";
                params.add("%" + keyword + "%");
            }

            if ("title".equals(type)) {
                sql += " and title like ?";
                params.add("%" + keyword + "%");
            }
        }
        Integer postCount = jdbcTemplate.queryForObject(sql, Integer.class, params.toArray());

        //null이면 0 아니면 postCount  /  return 할때 오토언박싱->postCount.intValue()
        return Objects.requireNonNullElse(postCount, 0);
    }

    /**
     * 지정된 ID에 해당하는 게시물을 찾아 반환합니다.
     * 게시글 본문 조회 후, 연관된 파일 정보들을 각각 채워서 반환합니다.
     *
     * @param id 조회할 게시물의 ID
     * @return 찾아진 게시물 객체, 없으면 null 반환
     */
    @Override
    public Post findById(Long id) {
        String sql = "select * from post where id = ?";
        try {
            Post post = jdbcTemplate.queryForObject(sql, postRowMapper(), id);
            if (post != null) {
                fillFiles(post);
            }
            return post;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 게시글 객체에 연관된 첨부파일과 이미지 파일들을 DB에서 조회하여 채워넣습니다.
     */
    private void fillFiles(Post post) {
        String sql = "select * from upload_file where post_id = ?";
        List<UploadFile> allFiles = jdbcTemplate.query(sql, fileRowMapper(), post.getId());

        allFiles.stream()
                .filter(f -> f.getFileType() == FileTypeEnum.ATTACHED)
                .findFirst()
                .ifPresent(post::setAttachFile);//.ifPresent(file -> post.setAttachFile(file));

        List<UploadFile> imageList = allFiles.stream()
                .filter(f -> f.getFileType() == FileTypeEnum.IMAGE)
                .collect(Collectors.toList());

        post.setImageFiles(imageList);
    }

    /**
     * 특정 회원이 작성한 모든 게시물을 조회합니다.
     *
     * @param memberId 조회할 회원의 ID
     * @return 해당 회원의 게시물 리스트
     */
    @Override
    public List<Post> findByMemberId(Long memberId) {
        String sql = "select * from post where author_id = ?";
        return jdbcTemplate.query(sql, postRowMapper(), memberId);
    }

    private RowMapper<Post> postRowMapper() {
        return BeanPropertyRowMapper.newInstance(Post.class);
    }

    private RowMapper<UploadFile> fileRowMapper() {
        return (rs, rowNum) -> {
            UploadFile file = new UploadFile();
            file.setId(rs.getLong("id"));
            file.setPostId(rs.getLong("post_id"));
            file.setStoreFileName(rs.getString("store_file_name"));
            file.setUploadFileName(rs.getString("upload_file_name"));

            String fileType = rs.getString("file_type");
            if (fileType != null) {
                file.setFileType(FileTypeEnum.valueOf(fileType));
            }
            return file;
        };
    }
}