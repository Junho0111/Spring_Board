package com.board.domain.login;

import com.board.domain.member.Member;
import com.board.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**로그인과 관련된 비즈니스 로직을 처리하는 서비스 클래스입니다.*/
@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    /**
     * 주어진 로그인 ID와 비밀번호로 로그인을 시도합니다.
     * *
     * @param loginId 사용자의 로그인 ID
     * @param password 사용자의 비밀번호
     * @return 로그인에 성공하면 해당 Member 객체를 반환하고, 실패하면 null을 반환합니다.
     */
    public Member login(String loginId, String password) {
        return memberRepository.findByLoginId(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }
}

