package com.board.domain.post;

import lombok.Data;
import lombok.NoArgsConstructor;

/**게시물 정보를 나타내는 도메인 객체*/
@Data
@NoArgsConstructor
public class Post {

    /** 게시물 고유 ID */
    private Long id;

    /** 게시물 제목 */
    private String title;

    /** 게시물 내용 */
    private String content;

    /** 게시물 작성자 */
    private String author;


    public Post(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }
}
