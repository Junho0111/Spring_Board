package com.board.domain.uploadfile;

import lombok.Data;

@Data
public class UploadFile {

    /** 유저가 업로드한 파일명 */
    private String uploadFileName;

    /** 서버 내부에서 관리하는 파일명 */
    private String storeFileName;

    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }
}
