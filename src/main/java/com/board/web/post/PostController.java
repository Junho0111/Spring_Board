package com.board.web.post;

import com.board.domain.comment.Comment;
import com.board.domain.comment.CommentRepository;
import com.board.domain.uploadfile.UploadFile;
import com.board.web.comment.form.CommentForm;
import com.board.domain.post.Post;
import com.board.domain.post.PostRepository;
import com.board.domain.member.Member;
import com.board.web.file.FileStore;
import com.board.web.post.form.PostForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.net.MalformedURLException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import java.nio.charset.StandardCharsets;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
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
    private final CommentRepository commentRepository;
    private final FileStore fileStore;

    /**
     * 지정된 파일 이름의 이미지를 응답 본문에 직접 반환합니다.
     * 주로 <img> 태그의 src 속성에서 이미지를 표시하는 데 사용됩니다.
     *
     * @param filename 서버에 저장된 이미지 파일의 이름
     * @return 이미지 파일에 대한 리소스
     * @throws MalformedURLException 파일 경로가 잘못된 형식일 경우
     */
    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }


    /**
     * 게시물에 첨부된 파일을 다운로드합니다.
     * Content-Disposition 헤더를 'attachment'로 설정하여 브라우저가 파일을 직접 표시하는 대신 다운로드 대화상자를 표시하도록 합니다.
     * 파일 이름은 UTF-8로 인코딩되어 다국어 문자가 깨지는 것을 방지합니다.
     *
     * @param postId 파일을 다운로드할 게시물의 ID
     * @return 다운로드할 파일과 HTTP 헤더를 포함하는 ResponseEntity
     * @throws MalformedURLException 파일 경로가 잘못된 형식일 경우
     */
    @GetMapping("/attach/{postId}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long postId) throws MalformedURLException {
        Post post = postRepository.findById(postId);
        String storeFileName = post.getAttachFile().getStoreFileName();
        String uploadFileName = post.getAttachFile().getUploadFileName();

        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFileName));
        log.info("uploadFileName={}", uploadFileName);

        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition).body(resource);
    }

    /**
     * 모든 게시물 목록을 조회하여 뷰에 전달한다.
     * <p>
     * GET 요청 {@code /posts}를 처리한다.
     *
     * @param model 뷰에 데이터를 전달하는 {@link Model} 객체
     * @return 게시물 목록 뷰의 논리적 이름 ({@code posts/posts})
     */
    @GetMapping
    public String posts(@SessionAttribute(name = "loginMember") Member loginMember, Model model) {
        List<Post> posts = postRepository.findAll();
        model.addAttribute("posts", posts);
        model.addAttribute("loginMember", loginMember);

        return "posts/posts";
    }

    /**
     * 특정 게시물 ID에 해당하는 게시물을 조회하여 뷰에 전달한다.
     * 댓글 목록과 댓글 작성을 위한 폼도 함께 전달한다.
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

        List<Comment> comments = commentRepository.findAllByPostId(postId);

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("commentForm", new CommentForm());

        return "posts/post";
    }
    /**
     * 새로운 게시물 등록을 위한 폼 화면을 출력한다.
     * <p>
     * {@code @ModelAttribute("post")}를 통해 빈 {@link PostForm} 객체를 생성하고,
     * 이를 "post"라는 이름으로 모델에 담아 뷰({@code posts/addForm})로 전달한다.
     *
     * @param form HTML 폼 바인딩을 위한 빈 {@link PostForm} 객체
     * @return 게시물 등록 폼의 뷰 논리적 이름
     */
    @GetMapping("/add")
    public String addForm(@ModelAttribute("post") PostForm form) {
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
    public String addPost(@Validated @ModelAttribute("post") PostForm form, BindingResult bindingResult, @SessionAttribute("loginMember") Member loginMember, RedirectAttributes redirectAttributes) throws IOException {

        if (bindingResult.hasErrors()) {
            return "posts/addForm";
        }

        UploadFile attachFile = fileStore.storeFile(form.getAttachFile());
        List<UploadFile> storeImageFiles = fileStore.storeFiles(form.getImageFiles());

        Post post = new Post();
        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
        post.setAuthor(loginMember.getName());
        post.setAuthorId(loginMember.getId());
        post.setAttachFile(attachFile);
        post.setImageFiles(storeImageFiles);

        postRepository.save(post);
        log.info("새 게시물 저장 완료 [ID={}, AuthorId={}, Author={}, Title={}]", post.getId(), post.getAuthorId(), post.getAuthor(), post.getTitle());

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
     * @param loginMember 세션에서 가져온 로그인 회원 객체 ({@code loginMember}). 게시물 수정 권한 검증에 사용.
     * @param redirectAttributes 리다이렉트 시 사용할 속성
     * @return 게시물 편집 폼 뷰의 논리적 이름 ({@code posts/editForm}) 또는 게시물 목록/상세 페이지로 리다이렉트 (권한 없거나 게시물 없을 시)
     */
    @GetMapping("/{postId}/edit")
    public String editForm(@PathVariable("postId") Long postId, Model model, @SessionAttribute("loginMember") Member loginMember, RedirectAttributes redirectAttributes) {
        Post post = postRepository.findById(postId);

        if (post == null) {
            log.warn("편집 요청된 게시물 ID[{}]를 찾을 수 없습니다.", postId);
            redirectAttributes.addFlashAttribute("errorMessage", "수정하려는 게시물을 찾을 수 없습니다.");
            return "redirect:/posts";
        }

        if (!post.getAuthorId().equals(loginMember.getId())) {
            log.warn("게시물 ID[{}]에 대한 수정 권한 없음. 요청자 ID: {}, 작성자 ID: {}", postId, loginMember.getLoginId(), post.getId());
            redirectAttributes.addFlashAttribute("errorMessage", "이 게시물을 수정할 권한이 없습니다.");
            return "redirect:/posts/{postId}";
        }

        PostForm postForm = new PostForm();
        postForm.setTitle(post.getTitle());
        postForm.setContent(post.getContent());

        model.addAttribute("postForm", postForm);
        model.addAttribute("post", post);
        model.addAttribute("postId", postId);
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
     * @param loginMember 세션에서 가져온 로그인 회원 객체 ({@code loginMember}). 게시물 수정 권한 검증에 사용.
     * @return 유효성 검증 실패 시 게시물 편집 폼으로 돌아가고, 성공 시 편집된 게시물 상세 페이지로 리다이렉트
     */
    @PostMapping("/{postId}/edit")
    public String edit(@PathVariable("postId") Long postId, @Validated @ModelAttribute("postForm") PostForm form, BindingResult bindingResult, @SessionAttribute("loginMember") Member loginMember, RedirectAttributes redirectAttributes) throws IOException {

        if (bindingResult.hasErrors()) {
            return "posts/editForm";
        }

        Post post = postRepository.findById(postId);

        if (post == null) {
            log.warn("편집 요청된 게시물 ID[{}]를 찾을 수 없습니다.", postId);
            return "redirect:/posts";
        }

        if (!post.getAuthorId().equals(loginMember.getId())) {
            log.warn("게시물[{}] ID[{}]에 대한 수정 권한 없음. 요청자: {}, 작성자: {}",post.getTitle(), postId, loginMember.getLoginId(), post.getAuthorId());
            redirectAttributes.addFlashAttribute("errorMessage", "이 게시물을 수정할 권한이 없습니다.");
            return "redirect:/posts";
        }

        UploadFile newAttachFile;
        if (form.getAttachFile() != null && !form.getAttachFile().isEmpty()) {
            newAttachFile = fileStore.storeFile(form.getAttachFile());
        } else {
            newAttachFile = post.getAttachFile();
        }

        List<UploadFile> newImageFiles;
        if (form.getImageFiles() != null && form.hasImageFiles()) {
            newImageFiles = fileStore.storeFiles(form.getImageFiles());
        } else {
            newImageFiles = post.getImageFiles();
        }

        postRepository.update(postId, form.getTitle(), form.getContent(), newAttachFile, newImageFiles);

        log.info("게시물 ID[{}] 업데이트 완료", postId);

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

        if (!post.getAuthorId().equals(loginMember.getId())) {
            log.warn("게시물[{}] ID[{}]에 대한 삭제 권한 없음. 요청자: {}, 작성자: {}",post.getTitle(), postId, loginMember.getLoginId(), post.getAuthorId());
            redirectAttributes.addFlashAttribute("errorMessage", "이 게시물을 삭제할 권한이 없습니다.");
            return "redirect:/posts/{postId}";
        }

        postRepository.delete(postId);
        log.info("게시물 ID[{}] 삭제 완료 [Author={}, Title={}}", postId, loginMember.getName(), post.getTitle());
        redirectAttributes.addFlashAttribute("successMessage", "게시물이 성공적으로 삭제되었습니다.");

        return "redirect:/posts";
    }

}