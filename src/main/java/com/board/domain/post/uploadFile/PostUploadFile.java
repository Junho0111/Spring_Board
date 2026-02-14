package com.board.domain.post.uploadFile;

import lombok.Data;

@Data
public class PostUploadFile {

    private String uploadFileName;
    private String storeFileName;

    public PostUploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }
}
