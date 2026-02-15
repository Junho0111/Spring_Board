package com.board.web.post.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 게시물 등록 폼 데이터 전송 객체 (DTO).
 * <p>
 * 게시물 제목, 내용, 이미지, 파일을 받아 유효성 검증을 수행한다.
 */
@Data
public class PostForm {

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 값입니다.")
    private String content;

    /** 이미지 파일 첨부 */
    private List<MultipartFile> imageFiles;

    /** 파일 첨부 */
    private MultipartFile attachFile;


    /** 이미지파일이 비어있는가 또는 비어있지 않은가 확인하는 메서드*/
    public boolean hasImageFiles() {
        if (this.imageFiles == null || this.imageFiles.isEmpty()) {
            return false;
        }

        return this.imageFiles.stream().anyMatch(files -> !files.isEmpty());
    }
}