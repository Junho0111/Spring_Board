package com.board.web.post.form;

import lombok.Data;

/** 게시물 검색 조건을 담는 폼 객체 */
@Data
public class PostSearchForm {

    /** 검색 타입 ("title", "author") */
    private String searchType;

    /** 검색 키워드 */
    private String keyword;
}
