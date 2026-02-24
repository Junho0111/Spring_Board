package com.board.web.post.form;

import lombok.Getter;


/** 페이징된 결과를 담는 클래스 */
@Getter
public class PagedResultForm {

    /** 전체 페이지 수 */
    private final int totalPage;

    /** 현재 페이지 */
    private final int currentPage;

    /** 현재 페이지가 속한 그룹의 시작 페이지 번호 */
    private final int startPage;

    /** 현재 페이지가 속한 그룹의 마지막 페이지 번호 */
    private final int endPage;

    /** 이전 페이지 존재 여부 */
    private final boolean hasPrevCheck;

    /** 다음 페이지 존재 여부 */
    private final boolean hasNextCheck;

    /** 한 화면에 보여줄 페이지 번호 개수 */
    private final int pageSize = 10;

    /**
     * 페이징 관련 값을 계산하는 생성자
     * @param totalPostCount 전체 게시글 수
     * @param currentPage 현재 페이지 번호
     * @param postsPerPage 한 페이지에 보여줄 게시글 수
     */
    public PagedResultForm(int totalPostCount, int currentPage, int postsPerPage) {
        this.currentPage = currentPage;

        this.totalPage = (int) Math.ceil((double) totalPostCount / postsPerPage);

        // 현재 페이지가 속한 그룹의 마지막 페이지 번호 계산
        int tempEndPage = (int) (Math.ceil((double) currentPage / pageSize)) * pageSize;

        // 현재 페이지가 속한 그룹의 시작 페이지 번호 계산
        this.startPage = tempEndPage - (pageSize - 1);

        // 총 페이지가 한 화면에 보여줄 페이지보다 적은경우
        if (tempEndPage > this.totalPage) {
            this.endPage = this.totalPage;
        } else {
            this.endPage = tempEndPage;
        }

        // 이전/다음 페이지 그룹 존재 여부 확인
        this.hasPrevCheck = this.startPage > 1;
        this.hasNextCheck = this.endPage < this.totalPage;
    }

}
