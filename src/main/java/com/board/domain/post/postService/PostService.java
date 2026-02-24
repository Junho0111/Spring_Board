package com.board.domain.post.postService;

import com.board.domain.post.Post;
import com.board.domain.post.PostRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepositoryImpl postRepository;

    public List<Post> findPosts(String type, String keyword,  int currentPage, int postsPerPage) {
        return postRepository.postSearchFindAll(type, keyword, currentPage, postsPerPage);
    }

    public int getTotalCount(String type, String keyword) {
        return postRepository.postSearchCount(type, keyword);
    }
}
