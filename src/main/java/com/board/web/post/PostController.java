package com.board.web.post;

import com.board.domain.post.Post;
import com.board.domain.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 게시물 관련 웹 요청을 처리하는 컨트롤러 클래스.
 * <p>
 * {@code /posts} 경로로 들어오는 요청을 처리한다.
 * 게시물 목록 조회 및 개별 게시물 상세 조회 기능을 제공한다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("posts")
public class PostController {

    private final PostRepository postRepository;

    /**
     * 모든 게시물 목록을 조회하여 뷰에 전달한다.
     * GET 요청 {@code /posts}를 처리한다.
     *
     * @param model 뷰에 데이터를 전달하는 {@link Model} 객체
     * @return 게시물 목록 뷰의 논리적 이름 ({@code posts/posts})
     */
    @GetMapping
    public String posts(Model model) {
        List<Post> posts = postRepository.findAll();
        model.addAttribute("posts", posts);
        return "posts/posts";
    }

    /**
     * 특정 게시물 ID에 해당하는 게시물을 조회하여 뷰에 전달한다.
     * GET 요청 {@code /posts/{postId}}를 처리한다.
     *
     * @param postId 조회할 게시물의 ID (URL 경로 변수 {@code postId})
     * @param model 뷰에 데이터를 전달하는 {@link Model} 객체
     * @return 특정 게시물 상세 뷰의 논리적 이름 ({@code posts/post})
     */
    @GetMapping("/{postId}")
    public String post(@PathVariable("postId") Long postId, Model model) {
        Post post = postRepository.findById(postId);
        model.addAttribute("post", post);
        return "posts/post";
    }
}
