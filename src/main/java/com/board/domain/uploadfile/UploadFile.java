package com.board.domain.uploadfile;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class UploadFile {

    /** 업로드파일 고유 ID */
    private Long id;

    /** 연결된 게시물 ID */
    private Long postId;

    /** 서버 내부에서 관리하는 파일명 */
    private String storeFileName;

    /** 유저가 업로드한 파일명 */
    private String uploadFileName;

    /** 이미지 또는 첨부파일 구분 */
    private FileTypeEnum fileType;

    public UploadFile(String uploadFileName, String storeFileName, FileTypeEnum fileType) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
        this.fileType = fileType;
    }
}