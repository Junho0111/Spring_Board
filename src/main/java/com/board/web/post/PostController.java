package com.board.web.post;

import com.board.domain.post.Post;
import com.board.domain.post.PostRepository;
import com.board.domain.member.Member;
import com.board.web.post.form.PostForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 게시물 관련 웹 요청을 처리하는 컨트롤러 클래스.
 * <p>
 * {@code /posts} 경로로 들어오는 요청을 처리한다.
 * 게시물 목록 조회, 개별 게시물 상세 조회, 게시물 생성 및 편집 기능을 제공한다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("posts")
public class PostController {

    private final PostRepository postRepository;

    /**
     * 모든 게시물 목록을 조회하여 뷰에 전달한다.
     * <p>
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
     * <p>
     * GET 요청 {@code /posts/{postId}}를 처리한다.
     *
     * @param postId 조회할 게시물의 ID (URL 경로 변수 {@code postId})
     * @param model 뷰에 데이터를 전달하는 {@link Model} 객체
     * @return 특정 게시물 상세 뷰의 논리적 이름 ({@code posts/post})
     */
    @GetMapping("/{postId}")
    public String post(@PathVariable("postId") Long postId, Model model) {
        Post post = postRepository.findById(postId);

        if (post == null) {
            log.warn("요청된 게시물 ID[{}]를 찾을 수 없습니다.", postId);
            return "redirect:/posts";
        }

        model.addAttribute("post", post);
        return "posts/post";
    }

    /**
     * 새로운 게시물을 생성하는 폼을 보여준다.
     * <p>
     * GET 요청 {@code /posts/add}를 처리한다.
     *
     * @param model 뷰에 {@link PostForm} 객체를 추가하여 폼 바인딩을 준비
     * @return 게시물 생성 폼 뷰의 논리적 이름 ({@code posts/addForm})
     */
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("post", new PostForm());

        return "posts/addForm";
    }

    /**
     * 새로운 게시물 생성 요청을 처리한다.
     * <p>
     * POST 요청 {@code /posts/add}를 처리하며, 폼 데이터 유효성 검증 후 게시물을 저장한다.
     * 작성자 정보는 세션에서 로그인된 회원 정보를 통해 가져온다.
     *
     * @param form 게시물 생성을 위한 폼 데이터 {@link PostForm}
     * @param bindingResult 유효성 검증 결과
     * @param loginMember 세션에서 가져온 로그인 회원 객체 ({@code loginMember}).
     * @param redirectAttributes 리다이렉트 시 사용할 속성
     * @return 유효성 검증 실패 시 게시물 생성 폼으로 돌아가고, 성공 시 생성된 게시물 상세 페이지로 리다이렉트
     */
    @PostMapping("/add")
    public String addPost(@Validated @ModelAttribute("post") PostForm form, BindingResult bindingResult, @SessionAttribute("loginMember") Member loginMember, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "posts/addForm";
        }

        Post post = new Post();
        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
        post.setAuthor(loginMember.getName());

        postRepository.save(post);
        log.info("새 게시물 저장 완료 [ID={}, Author={}, Title={}]", post.getId(), post.getAuthor(), post.getTitle());

        redirectAttributes.addAttribute("postId", post.getId());
        return "redirect:/posts/{postId}";
    }

    /**
     * 기존 게시물을 편집하는 폼을 보여준다.
     * <p>
     * GET 요청 {@code /posts/{postId}/edit}를 처리한다.
     * 게시물 ID를 통해 기존 게시물 정보를 조회하고, 해당 정보를 폼에 채워 반환한다.
     *
     * @param postId 편집할 게시물의 ID (URL 경로 변수 {@code postId})
     * @param model 뷰에 데이터를 전달하는 {@link Model} 객체
     * @return 게시물 편집 폼 뷰의 논리적 이름 ({@code posts/editForm}) 또는 게시물 목록 페이지로 리다이렉트 (게시물 없을 시)
     */
    @GetMapping("/{postId}/edit")
    public String editForm(@PathVariable("postId") Long postId, Model model) {
        Post post = postRepository.findById(postId);

        if (post == null) {
            log.warn("편집 요청된 게시물 ID[{}]를 찾을 수 없습니다.", postId);

            return "redirect:/posts";
        }

        PostForm postForm = new PostForm();
        postForm.setTitle(post.getTitle());
        postForm.setContent(post.getContent());
        model.addAttribute("post", postForm);

        return "posts/editForm";
    }

    /**
     * 게시물 편집 요청을 처리한다.
     * <p>
     * POST 요청 {@code /posts/{postId}/edit}를 처리하며, 폼 데이터 유효성 검증 후 게시물을 업데이트한다.
     *
     * @param postId 편집할 게시물의 ID (URL 경로 변수 {@code postId})
     * @param form 게시물 편집을 위한 폼 데이터 {@link PostForm}
     * @param bindingResult 유효성 검증 결과
     * @param redirectAttributes 리다이렉트 시 사용할 속성
     * @return 유효성 검증 실패 시 게시물 편집 폼으로 돌아가고, 성공 시 편집된 게시물 상세 페이지로 리다이렉트
     */
    @PostMapping("/{postId}/edit")
    public String edit(@PathVariable("postId") Long postId, @Validated @ModelAttribute("post") PostForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "posts/editForm";
        }

        Post post = postRepository.findById(postId);

        if (post == null) {
            log.warn("편집 요청된 게시물 ID[{}]를 찾을 수 없습니다.", postId);

            return "redirect:/posts";
        }

        postRepository.update(postId, form.getTitle(), form.getContent());
        log.info("게시물 ID[{}] 업데이트 완료 [Author={}, newTitle={}]", postId, post.getAuthor(), form.getTitle());

        redirectAttributes.addAttribute("postId", postId);
        return "redirect:/posts/{postId}";
    }

    /**
     * 게시물 삭제 요청을 처리한다.
     * <p>
     * POST 요청 {@code /posts/{postId}/delete}를 처리하며, 해당 게시물의 작성자만 삭제할 수 있다.
     *
     * @param postId 삭제할 게시물의 ID (URL 경로 변수 {@code postId})
     * @param loginMember 세션에서 가져온 로그인 회원 객체 ({@code loginMember}). 게시물 삭제 권한 검증에 사용.
     * @param redirectAttributes 리다이렉트 시 사용할 속성
     * @return 게시물 삭제 성공 시 게시물 목록 페이지로 리다이렉트. 실패 시 경고 메시지와 함께 현재 페이지로 리다이렉트.
     */
    @PostMapping("/{postId}/delete")
    public String delete(@PathVariable("postId") Long postId, @SessionAttribute("loginMember") Member loginMember, RedirectAttributes redirectAttributes) {

        Post post = postRepository.findById(postId);
        if (post == null) {
            log.warn("삭제 요청된 게시물 ID[{}]를 찾을 수 없습니다.", postId);
            redirectAttributes.addFlashAttribute("errorMessage", "삭제하려는 게시물을 찾을 수 없습니다.");
            return "redirect:/posts";
        }

        if (!post.getAuthor().equals(loginMember.getName())) {
            log.warn("게시물 ID[{}]에 대한 삭제 권한 없음. 요청자: {}, 작성자: {}", postId, loginMember.getName(), post.getAuthor());
            redirectAttributes.addFlashAttribute("errorMessage", "이 게시물을 삭제할 권한이 없습니다.");
            return "redirect:/posts/{postId}";
        }

        postRepository.delete(postId);
        log.info("게시물 ID[{}] 삭제 완료 [Author={}, Title={}}", postId, loginMember.getName(), post.getTitle());
        redirectAttributes.addFlashAttribute("successMessage", "게시물이 성공적으로 삭제되었습니다.");

        return "redirect:/posts";
    }

}

