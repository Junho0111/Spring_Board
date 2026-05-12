package com.board.domain.post;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * MyBatis 매퍼 인터페이스입니다.
 * postMapper.xml에 정의된 SQL 쿼리와 연결됩니다.
 */
@Mapper
public interface PostRepositoryMybatis {

    /**
     * 게시물을 저장합니다.
     * @param post 저장할 게시물 객체
     */
    void save(Post post);

    /**
     * 게시물 제목과 내용을 업데이트합니다.
     * @param id 게시물 ID
     * @param title 변경할 제목
     * @param content 변경할 내용
     * @return 영향받은 행의 수
     */
    int update(@Param("id") Long id, @Param("title") String title, @Param("content") String content);

    /**
     * 게시물 작성자명을 업데이트합니다.
     * @param id 게시물 ID
     * @param author 변경할 작성자 이름
     */
    void updateAuthor(@Param("id") Long id, @Param("author") String author);

    /**
     * 게시물을 삭제합니다.
     * @param id 삭제할 게시물 ID
     */
    void delete(Long id);

    /**
     * 모든 게시물을 조회합니다.
     * @return 게시물 리스트
     */
    List<Post> findAll();

    /**
     * 검색 조건 및 페이징을 적용하여 게시물을 조회합니다.
     * @param type 검색 타입 (author, title)
     * @param keyword 검색어
     * @param offset 시작 위치
     * @param limit 가져올 개수
     * @return 검색된 게시물 리스트
     */
    List<Post> postSearchFindAll(@Param("type") String type, @Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 검색 조건에 맞는 게시물의 총 개수를 조회합니다.
     * @param type 검색 타입
     * @param keyword 검색어
     * @return 총 개수
     */
    int postSearchCount(@Param("type") String type, @Param("keyword") String keyword);

    /**
     * ID로 게시물을 조회합니다.
     * @param id 조회할 게시물 ID
     * @return 조회된 게시물 객체
     */
    Post findById(Long id);

    /**
     * 특정 회원이 작성한 모든 게시물을 조회합니다.
     * @param memberId 회원 ID
     * @return 게시물 리스트
     */
    List<Post> findByMemberId(Long memberId);
}
