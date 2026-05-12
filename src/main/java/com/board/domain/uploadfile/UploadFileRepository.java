package com.board.domain.uploadfile;

import java.util.List;

/**
 * 파일 데이터에 접근하기 위한 리포지토리 인터페이스입니다.
 */
public interface UploadFileRepository {

    /**
     * 파일 정보를 저장합니다.
     * @param file 저장할 파일 객체
     */
    void save(UploadFile file);

    /**
     * 특정 게시물에 속한 모든 파일을 삭제합니다.
     * @param postId 게시물 ID
     */
    void deleteByPostId(Long postId);

    /**
     * 특정 게시물에 속한 모든 파일 목록을 조회합니다.
     * @param postId 게시물 ID
     * @return 파일 리스트
     */
    List<UploadFile> findAllByPostId(Long postId);
}
