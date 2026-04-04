package com.board.util.file;

/**
 * 파일 저장 및 입출력 중 발생하는 체크예외를 처리하기 위한 커스텀 언체크 예외입니다.
 * RuntimeException을 상속받고 예외가 터진곳을 알기위해 원인을 파라미터로 받습니다
 */
public class FileStoreException extends RuntimeException {
    public FileStoreException() {
    }

    public FileStoreException(String message) {
        super(message);
    }

    public FileStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileStoreException(Throwable cause) {
        super(cause);
    }
}
