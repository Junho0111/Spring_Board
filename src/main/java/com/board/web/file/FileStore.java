package com.board.web.file;

import com.board.domain.uploadfile.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {

    /** 파일 및 이미지가 저장될 경로 */
    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String fileName) {
        return fileDir + fileName;
    }

    /**
     * 다중 파일 저장을 처리합니다.
     * 각 MultipartFile을 순회하며, 비어 있지 않은 경우 storeFile 메서드를 호출하여 저장합니다.
     *
     * @param multipartFiles 사용자가 업로드한 파일 리스트
     * @return 저장된 파일 정보(UploadFile) 리스트
     * @throws IOException 파일 저장 중 오류 발생 시
     */
    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadFile> storeFileResult = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            if(!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile));
            }
        }
        return storeFileResult;
    }

    /**
     * 단일 파일을 지정된 경로에 저장합니다.
     * 파일이 비어있으면 null을 반환하고, 그렇지 않으면 서버 내부에서 사용할 고유한 파일명을 생성하여 저장합니다.
     *
     * @param multipartFile 사용자가 업로드한 단일 파일
     * @return 저장된 파일의 정보(UploadFile)
     * @throws IOException 파일 저장 중 오류 발생 시
     */
    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        multipartFile.transferTo(new File(getFullPath(storeFileName)));
        return new UploadFile(originalFilename, storeFileName);
    }

    /**
     * 서버 내부에서 관리하는 파일명은 유일한 이름을 생성하는 UUID를 사용하여 충돌하지 않도록 한다.
     *
     * @param originalFilename 사용자가 입력한 파일명
     * @return 서버내에서 충돌이 나지 않는 이름 반환
     */
    private static String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();

        return uuid + "." + ext;
    }

    /**
     * 확장자를 별도로 추출하여 서버내에서 관리하는 파일명에 붙여 줍니다
     *
     * @param originalFilename 사용자가 입력한 파일명
     */
    private static String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");

        return originalFilename.substring(pos + 1);
    }
}
