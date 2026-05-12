package com.board.domain.uploadfile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MyBatis를 사용하여 데이터베이스의 upload_file 테이블에 접근하는 구현체입니다.
 * {@link UploadFileRepository} 인터페이스를 구현하며, 실제 SQL 쿼리 실행은 {@link UploadFileRepositoryMybatis} 매퍼에 위임합니다.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UploadFileRepositoryMybatisImpl implements UploadFileRepository {

    private final UploadFileRepositoryMybatis uploadFileMapper;

    /**
     * 파일 정보를 데이터베이스에 저장합니다.
     * MyBatis 매퍼를 통해 저장을 실행하며, 생성된 고유 ID가 객체에 할당됩니다.
     * @param file 저장할 파일 객체
     */
    @Override
    public void save(UploadFile file) {
        uploadFileMapper.save(file);
        log.info("MyBatis FILE SAVE [ID={}, PostID={}, Type={}]", file.getId(), file.getPostId(), file.getFileType());
    }

    /**
     * 특정 게시물에 연결된 모든 파일 정보를 데이터베이스에서 삭제합니다.
     * @param postId 삭제할 파일들이 속한 게시물의 고유 ID
     */
    @Override
    public void deleteByPostId(Long postId) {
        uploadFileMapper.deleteByPostId(postId);
        log.info("MyBatis FILES DELETED [PostID={}]", postId);
    }

    /**
     * 특정 게시물에 첨부된 모든 파일 목록을 조회합니다.
     * @param postId 조회할 게시물의 고유 ID
     * @return 해당 게시물의 파일 리스트
     */
    @Override
    public List<UploadFile> findAllByPostId(Long postId) {
        return uploadFileMapper.findAllByPostId(postId);
    }
}
