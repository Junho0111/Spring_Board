package com.board.domain.post.postService;

import com.board.domain.post.Post;
import com.board.domain.post.PostRepository;
import com.board.domain.uploadfile.FileTypeEnum;
import com.board.domain.uploadfile.UploadFile;
import com.board.domain.uploadfile.UploadFileRepositoryJdbc;
import com.board.util.file.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 게시물과 관련된 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 데이터베이스 작업(Repository)과 파일 저장 작업(FileStore)을 하나의 트랜잭션으로 묶어 관리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UploadFileRepositoryJdbc uploadFileRepository;
    private final FileStore fileStore;

    /**
     * 검색 조건과 페이징 설정을 기준으로 게시물 목록을 조회합니다.
     * 읽기 전용 트랜잭션으로 설정하여 조회 성능을 최적화합니다.
     *
     * @param type         검색할 타입 (예: title, author)
     * @param keyword      검색할 키워드
     * @param currentPage  현재 페이지 번호
     * @param postsPerPage 한 페이지에 보여줄 게시물 수
     * @return 검색 조건에 맞는 게시물 리스트
     */
    @Transactional(readOnly = true)
    public List<Post> findPosts(String type, String keyword, int currentPage, int postsPerPage) {
        return postRepository.postSearchFindAll(type, keyword, currentPage, postsPerPage);
    }

    /**
     * 검색 조건에 맞는 전체 게시물 개수를 반환합니다. 주로 페이징 처리를 위해 사용됩니다.
     *
     * @param type    검색할 타입
     * @param keyword 검색할 키워드
     * @return 검색 조건에 일치하는 전체 게시글 수
     */
    @Transactional(readOnly = true)
    public int getTotalCount(String type, String keyword) {
        return postRepository.postSearchCount(type, keyword);
    }

    /**
     * 지정된 ID에 해당하는 단일 게시물의 상세 정보를 조회합니다.
     * 연관된 첨부파일 및 이미지 파일 정보도 함께 조회되어 반환됩니다.
     *
     * @param id 조회할 게시물의 고유 ID
     * @return 조회된 게시물 객체, 존재하지 않을 경우 null
     */
    @Transactional(readOnly = true)
    public Post getPost(Long id) {
        return postRepository.findById(id);
    } //findById 나중에 리펙터링할것

    /**
     * 새로운 게시물과 업로드된 파일들을 함께 저장합니다.
     * 물리적 파일 저장과 DB 저장이 하나의 트랜잭션으로 묶여 있어 중간에 실패할 경우 전체 롤백됩니다.
     *
     * @param post       저장할 게시물 본문 정보
     * @param attachFile 업로드된 단일 첨부파일
     * @param imageFiles 업로드된 다중 이미지 파일 목록
     * @return 데이터베이스에 저장되고 생성된 게시물의 고유 ID
     */
    @Transactional
    public Long savePost(Post post, MultipartFile attachFile, List<MultipartFile> imageFiles) {
        Post savedPost = postRepository.save(post);
        Long postId = savedPost.getId();

        UploadFile uploadAttachFile = fileStore.storeFile(attachFile, FileTypeEnum.ATTACHED);
        if (uploadAttachFile != null) {
            uploadAttachFile.setPostId(postId);
            uploadFileRepository.save(uploadAttachFile);
        }

        List<UploadFile> uploadImageFiles = fileStore.storeFiles(imageFiles, FileTypeEnum.IMAGE);
        for (UploadFile uploadImageFile : uploadImageFiles) {
            uploadImageFile.setPostId(postId);
            uploadFileRepository.save(uploadImageFile);
        }

        return postId;
    }

    /**
     * 기존 게시물의 본문을 수정하고, 새로운 파일이 업로드된 경우 기존 파일을 대체합니다.
     * 새로운 파일이 전달되지 않은 경우 기존 파일 정보는 그대로 유지됩니다.
     *
     * @param postId        수정할 게시물의 고유 ID
     * @param title         수정할 제목
     * @param content       수정할 내용
     * @param newAttachFile 새롭게 업로드된 첨부파일 (기존 첨부파일 완전 교체 목적)
     * @param newImageFiles 새롭게 업로드된 이미지 파일 목록 (기존 이미지파일들 완전 교체 목적)
     */
    @Transactional
    public void updatePost(Long postId, String title, String content, MultipartFile newAttachFile, List<MultipartFile> newImageFiles) {
        postRepository.update(postId, title, content);

        boolean hasNewAttach = fileChecker(newAttachFile);
        boolean hasNewImages = filesChecker(newImageFiles);

        if (hasNewAttach || hasNewImages) {
            uploadFileRepository.deleteByPostId(postId);

            if (hasNewAttach) {
                UploadFile attachFile = fileStore.storeFile(newAttachFile, FileTypeEnum.ATTACHED);
                attachFile.setPostId(postId);
                uploadFileRepository.save(attachFile);
            }

            if (hasNewImages) {
                List<UploadFile> images = fileStore.storeFiles(newImageFiles, FileTypeEnum.IMAGE);
                for (UploadFile image : images) {
                    image.setPostId(postId);
                    uploadFileRepository.save(image);
                }
            }
        }
    }

    /**
     * 단일 파일이 정상적으로 업로드되었는지 확인하는 내부 유틸리티 메서드입니다.
     * @param newAttachFile 검증할 파일 객체
     * @return 파일이 존재하고 비어있지 않은 경우 true 반환
     */
    private static boolean fileChecker(MultipartFile newAttachFile) {
        return newAttachFile != null && !newAttachFile.isEmpty();
    }//나중에 코드 리펙터링했을때를 위한 !newAttachFile.isEmpty() / queryForList, query

    /**
     * 다중 파일 목록이 정상적으로 업로드되었는지 확인하는 내부 유틸리티 메서드입니다.
     * @param newImageFiles 검증할 파일 리스트
     * @return 리스트가 존재하고 비어있지 않은 경우 true 반환
     */
    private static boolean filesChecker(List<MultipartFile> newImageFiles) {
        return newImageFiles != null && !newImageFiles.isEmpty();
    }

    /**
     * 지정된 ID의 게시물을 삭제합니다.
     * 데이터베이스의 외래키 제약조건(ON DELETE CASCADE) 설정에 따라 연관된 파일 정보도 함께 삭제됩니다.
     *
     * @param postId 삭제할 게시물의 고유 ID
     */
    @Transactional
    public void deletePost(Long postId) {
        postRepository.delete(postId);
    }
}