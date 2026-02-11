package com.board.web.comment;

import com.board.domain.comment.Comment;
import com.board.domain.comment.CommentRepository;
import com.board.domain.member.Member;
import com.board.domain.post.Post;
import com.board.domain.post.PostRepository;
import com.board.web.comment.form.CommentForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * 댓글 관련 웹 요청을 처리하는 컨트롤러 클래스.
 * 특정 게시물에 대한 댓글 추가 및 답글 추가 기능을 제공합니다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    /**
     * 게시물에 댓글 또는 답글을 추가합니다.
     *
     * @param postId 댓글을 추가할 게시물의 ID
     * @param form 댓글 내용 및 부모 댓글 ID를 포함하는 {@link CommentForm}
     * @param bindingResult 유효성 검증 결과
     * @param loginMember 세션에서 가져온 로그인 회원 객체 (댓글 작성자 정보)
     * @param redirectAttributes 리다이렉트 시 사용할 속성
     * @return 게시물 상세 페이지로 리다이렉트
     */
    @PostMapping
    public String addComment(@PathVariable Long postId, @Validated @ModelAttribute("commentForm") CommentForm form, BindingResult bindingResult, @SessionAttribute(name = "loginMember") Member loginMember, RedirectAttributes redirectAttributes) {

        Post post = postRepository.findById(postId);
        if (post == null) {
            log.warn("존재하지 않는 게시물 ID[{}]에 댓글 추가 시도", postId);
            redirectAttributes.addFlashAttribute("errorMessage", "댓글을 추가하려는 게시물이 존재하지 않습니다.");
            return "redirect:/posts";
        }

        if (bindingResult.hasErrors()) {
            return "redirect:/posts/" + postId;
        }

        Comment comment = new Comment();

        if (form.getParentCommentId() != null) {
            Comment parentComment = commentRepository.findById(form.getParentCommentId());
            if (parentComment == null || !parentComment.getPostId().equals(postId)) {
                log.warn("유효하지 않은 부모 댓글 ID[{}] 또는 게시물 ID[{}]와 일치하지 않는 부모 댓글에 답글 시도", form.getParentCommentId(), postId);
                redirectAttributes.addFlashAttribute("errorMessage", "유효하지 않은 부모 댓글입니다.");
                return "redirect:/posts/" + postId;
            }
            comment = new Comment(postId, form.getParentCommentId(), loginMember.getName(), loginMember.getId(), form.getContent());
            log.info("대댓글 저장 시도 [PostId={}, ParentCommentId={}, Author={}, Content={}]", postId, form.getParentCommentId(), loginMember.getName(), form.getContent());
        } else {
            comment = new Comment(postId, loginMember.getName(), loginMember.getId(), form.getContent());
            log.info("댓글 저장 시도 [PostId={}, Author={}, Content={}]", postId, loginMember.getName(), form.getContent());
        }

        commentRepository.save(comment);
        redirectAttributes.addFlashAttribute("successMessage", "댓글이 성공적으로 작성되었습니다.");

        return "redirect:/posts/" + postId;
    }


    /**
     * 댓글 수정 폼을 제공합니다.
     *
     * @param postId 게시물 ID
     * @param commentId 수정할 댓글 ID
     * @param model 뷰에 데이터를 전달하는 Model 객체
     * @param loginMember 세션에서 가져온 로그인 회원 객체 (권한 확인용)
     * @param redirectAttributes 리다이렉트 시 사용할 속성
     * @return 댓글 수정 폼 뷰 또는 리다이렉트 경로
     */
    @GetMapping("/{commentId}/edit")
    public String editForm(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, Model model, @SessionAttribute(name = "loginMember") Member loginMember, RedirectAttributes redirectAttributes) {
        Comment comment = commentRepository.findById(commentId);

        if (comment == null) {
            log.warn("존재하지 않는 댓글 ID[{}]에 대한 수정 폼 요청", commentId);
            redirectAttributes.addFlashAttribute("errorMessage", "수정하려는 댓글이 존재하지 않습니다.");
            return "redirect:/posts/" + postId;
        }

        if (!comment.getPostId().equals(postId)) {
            log.warn("댓글 ID[{}]가 게시물 ID[{}]와 일치하지 않습니다.", commentId, postId);
            redirectAttributes.addFlashAttribute("errorMessage", "잘못된 접근입니다.");
            return "redirect:/posts/" + postId;
        }

        if (!comment.getAuthorId().equals(loginMember.getId())) {
            log.warn("댓글 작성자 ID[{}]와 로그인한 사용자 ID[{}]가 일치하지 않아 수정 권한 없음", comment.getAuthorId(), loginMember.getId());
            redirectAttributes.addFlashAttribute("errorMessage", "댓글을 수정할 권한이 없습니다.");
            return "redirect:/posts/" + postId;
        }

        CommentForm commentForm = new CommentForm();
        commentForm.setContent(comment.getContent());
        model.addAttribute("commentForm", commentForm);
        model.addAttribute("postId", postId);
        model.addAttribute("commentId", commentId);

        return "comments/editForm";
    }



    /**
     * 댓글 수정 요청을 처리합니다.
     *
     * @param postId 게시물 ID
     * @param commentId 수정할 댓글 ID
     * @param form 댓글 내용 및 부모 댓글 ID를 포함하는 {@link CommentForm}
     * @param bindingResult 유효성 검증 결과
     * @param loginMember 세션에서 가져온 로그인 회원 객체 (권한 확인용)
     * @param redirectAttributes 리다이렉트 시 사용할 속성
     * @return 게시물 상세 페이지로 리다이렉트 또는 댓글 수정 폼 뷰
     */
    @PostMapping("/{commentId}/edit")
    public String editComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, @Validated @ModelAttribute("commentForm") CommentForm form, BindingResult bindingResult, @SessionAttribute("loginMember") Member loginMember, RedirectAttributes redirectAttributes) {
        Comment comment = commentRepository.findById(commentId);

        if (comment == null) {
            log.warn("존재하지 않는 댓글 ID[{}]에 대한 수정 요청", commentId);
            redirectAttributes.addFlashAttribute("errorMessage", "수정하려는 댓글이 존재하지 않습니다.");
            return "redirect:/posts/" + postId;
        }

        if (!comment.getPostId().equals(postId)) {
            log.warn("댓글 ID[{}]가 게시물 ID[{}]와 일치하지 않습니다.", commentId, postId);
            redirectAttributes.addFlashAttribute("errorMessage", "잘못된 접근입니다.");
            return "redirect:/posts/" + postId;
        }

        if (!comment.getAuthorId().equals(loginMember.getId())) {
            log.warn("댓글 작성자 ID[{}]와 로그인한 사용자 ID[{}]가 일치하지 않아 수정 권한 없음", comment.getAuthorId(), loginMember.getId());
            redirectAttributes.addFlashAttribute("errorMessage", "댓글을 수정할 권한이 없습니다.");
            return "redirect:/posts/" + postId;
        }

        if (bindingResult.hasErrors()) {
            return "comments/editForm"; 
        }
        
        commentRepository.update(commentId, form.getContent());
        redirectAttributes.addFlashAttribute("successMessage", "댓글이 성공적으로 수정되었습니다.");
        
        return "redirect:/posts/" + postId;
    }



    /**
     * 댓글 삭제 요청을 처리합니다.
     *
     * @param postId 게시물 ID
     * @param commentId 삭제할 댓글 ID
     * @param loginMember 세션에서 가져온 로그인 회원 객체 (권한 확인용)
     * @param redirectAttributes 리다이렉트 시 사용할 속성
     * @return 게시물 상세 페이지로 리다이렉트
     */
    @PostMapping("/{commentId}/delete")
    public String delete(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, @SessionAttribute("loginMember") Member loginMember, RedirectAttributes redirectAttributes) {
        Comment comment = commentRepository.findById(commentId);

        if (comment == null) {
            log.warn("존재하지 않는 댓글 ID[{}]에 대한 삭제 요청", commentId);
            redirectAttributes.addFlashAttribute("errorMessage", "삭제하려는 댓글이 존재하지 않습니다.");
            return "redirect:/posts/" + postId;
        }

        if (!comment.getPostId().equals(postId)) {
            log.warn("댓글 ID[{}]가 게시물 ID[{}]와 일치하지 않습니다.", commentId, postId);
            redirectAttributes.addFlashAttribute("errorMessage", "잘못된 접근입니다.");
            return "redirect:/posts/" + postId;
        }

        if (!comment.getAuthorId().equals(loginMember.getId())) {
            log.warn("댓글 작성자 ID[{}]와 로그인한 사용자 ID[{}]가 일치하지 않아 삭제 권한 없음", comment.getAuthorId(), loginMember.getId());
            redirectAttributes.addFlashAttribute("errorMessage", "댓글을 삭제할 권한이 없습니다.");
            return "redirect:/posts/" + postId;
        }

        List<Long> commentsToDelete = new ArrayList<>();
        commentsToDelete.add(commentId);

        List<Long> descendantCommentIds = commentRepository.findAllDescendantCommentIds(commentId);
        commentsToDelete.addAll(descendantCommentIds);

        commentRepository.deleteAllByIds(commentsToDelete);
        redirectAttributes.addFlashAttribute("successMessage", "댓글이 성공적으로 삭제되었습니다.");

        return "redirect:/posts/" + postId;
    }
}