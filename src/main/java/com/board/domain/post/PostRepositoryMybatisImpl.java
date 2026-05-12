package com.board.domain.post;

import com.board.domain.uploadfile.FileTypeEnum;
import com.board.domain.uploadfile.UploadFile;
import com.board.domain.uploadfile.UploadFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MyBatis를 사용하여 데이터베이스의 Post 테이블에 접근하는 구현체입니다.
 * {@link PostRepository} 인터페이스를 구현하며, 실제 쿼리는 {@link PostRepositoryMybatis} 매퍼에 위임합니다.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PostRepositoryMybatisImpl implements PostRepository {

    private final PostRepositoryMybatis postMapper;
    private final UploadFileRepository fileMapper;

    /**
     * 새로운 게시물을 저장합니다.
     * @param post 저장할 게시물 객체
     * @return 저장된 게시물 객체 (생성된 ID 포함)
     */
    @Override
    public Post save(Post post) {
        postMapper.save(post);
        log.info("MyBatis SAVE [ID={}, Author={}, Title={}]", post.getId(), post.getAuthor(), post.getTitle());
        return post;
    }

    /**
     * 게시물 정보를 수정합니다.
     * @param id 수정할 게시물 ID
     * @param title 변경할 제목
     * @param content 변경할 내용
     * @throws IllegalArgumentException 해당 ID의 게시물이 존재하지 않을 경우 발생
     */
    @Override
    public void update(Long id, String title, String content) {
        int updated = postMapper.update(id, title, content);
        if (updated == 0) {
            log.error("UPDATE FAILED: ID {} NOT FOUND", id);
            throw new IllegalArgumentException("수정 실패: 해당 ID(" + id + ")의 게시물이 존재하지 않습니다.");
        }
    }

    /**
     * 게시물 작성자 이름을 업데이트합니다.
     * @param id 수정할 게시물 ID
     * @param author 변경할 새 작성자 이름
     */
    @Override
    public void updateAuthor(Long id, String author) {
        postMapper.updateAuthor(id, author);
    }

    /**
     * 고유 식별자(ID)를 기준으로 게시물을 삭제합니다.
     * DB의 ON DELETE CASCADE 설정으로 인해 연관된 파일들도 자동 삭제될 수 있습니다.
     * @param id 삭제할 게시물 ID
     * @return 삭제되기 전의 게시물 객체 정보
     */
    @Override
    public Post delete(Long id) {
        Post post = findById(id);
        if (post != null) {
            postMapper.delete(id);
            log.info("MyBatis DELETED [ID={}]", id);
        }
        return post;
    }

    /**
     * 모든 게시물 목록을 조회합니다.
     * @return 전체 게시물 리스트
     */
    @Override
    public List<Post> findAll() {
        return postMapper.findAll();
    }

    /**
     * 검색 조건 및 페이징 정보를 기반으로 게시물을 조회합니다.
     * @param type 검색 타입 (author, title)
     * @param keyword 검색어
     * @param currentPage 현재 페이지 번호
     * @param postsPerPage 페이지당 게시물 수
     * @return 페이징 처리된 게시물 리스트
     */
    @Override
    public List<Post> postSearchFindAll(String type, String keyword, int currentPage, int postsPerPage) {
        int offset = (currentPage - 1) * postsPerPage;
        return postMapper.postSearchFindAll(type, keyword, offset, postsPerPage);
    }

    /**
     * 검색 조건에 일치하는 게시물의 총 개수를 조회합니다.
     * @param type 검색 타입
     * @param keyword 검색어
     * @return 총 게시물 수
     */
    @Override
    public int postSearchCount(String type, String keyword) {
        return postMapper.postSearchCount(type, keyword);
    }

    /**
     * ID를 기준으로 게시물 하나를 상세 조회합니다.
     * 연관된 첨부파일 및 이미지 파일 정보도 함께 조회하여 채워넣습니다.
     * @param id 조회할 게시물 ID
     * @return 조회된 게시물 객체 (파일 정보 포함)
     */
    @Override
    public Post findById(Long id) {
        Post post = postMapper.findById(id);
        if (post != null) {
            fillFiles(post);
        }
        return post;
    }

    /**
     * 게시물에 연관된 파일들을 조회하여 객체에 설정합니다.
     */
    private void fillFiles(Post post) {
        List<UploadFile> allFiles = fileMapper.findAllByPostId(post.getId());

        allFiles.stream()
                .filter(f -> f.getFileType() == FileTypeEnum.ATTACHED)
                .findFirst()
                .ifPresent(post::setAttachFile);

        List<UploadFile> imageList = allFiles.stream()
                .filter(f -> f.getFileType() == FileTypeEnum.IMAGE)
                .collect(Collectors.toList());

        post.setImageFiles(imageList);
    }

    /**
     * 특정 회원이 작성한 모든 게시물을 조회합니다.
     * @param memberId 회원 ID
     * @return 해당 회원의 게시물 리스트
     */
    @Override
    public List<Post> findByMemberId(Long memberId) {
        return postMapper.findByMemberId(memberId);
    }
}
