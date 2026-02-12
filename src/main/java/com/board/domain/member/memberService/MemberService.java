package com.board.domain.member.memberService;

import com.board.domain.comment.CommentRepository;
import com.board.domain.member.Member;
import com.board.domain.member.MemberRepository;
import com.board.domain.post.Post;
import com.board.domain.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 회원 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    /**
     * 새로운 회원을 가입시킵니다.
     * 동일한 로그인 ID를 가진 회원이 이미 존재하면 {@link IllegalStateException}을 발생시킵니다.
     * @param member 가입할 회원 정보
     * @return 가입된 회원 객체
     * @throws IllegalStateException 이미 존재하는 아이디인 경우
     */
    public Member join(Member member) {
        memberRepository.findByLoginId(member.getLoginId())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 아이디입니다.");
                });

        return memberRepository.save(member);
    }

    /**
     * 기존 회원을 탈퇴시킴.
     * @param memberId 탈퇴할 회원의 아이디
     */
    public void deleteMember(Long memberId) {
        List<Post> posts = postRepository.findByMemberId(memberId);
        for (Post post : posts) {
            commentRepository.deleteByPostId(post.getId());
            postRepository.delete(post.getId());
        }
        memberRepository.delete(memberId);
    }

    /**
     * 모든 회원 목록을 조회합니다.
     * @return 모든 회원 정보를 담은 리스트
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * 주어진 ID로 회원을 조회합니다.
     * @param id 조회할 회원의 ID
     * @return 조회된 회원 객체
     */
    public Member findMemberById (Long id) {
        return memberRepository.findById(id);
    }
}
