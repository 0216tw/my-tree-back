package com.example.mytree.service;

import java.util.List;

import com.example.mytree.domain.Post;
import com.example.mytree.dto.PostCreateRequest;
import com.example.mytree.dto.PostResponse;
import com.example.mytree.exception.PostNotFoundException;
import com.example.mytree.exception.UserNotFoundException;
import com.example.mytree.repository.PostRepository;
import com.example.mytree.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

	private final PostRepository postRepository;
	private final UserRepository userRepository;

	public PostService(PostRepository postRepository, UserRepository userRepository) {
		this.postRepository = postRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public PostResponse createPost(PostCreateRequest request) {
		validateUserExists(request.userId());

		Post post = new Post();
		post.setUserId(request.userId());
		post.setTitle(request.title());
		post.setContent(request.content());

		postRepository.insertPost(post);

		return toResponse(loadExistingPost(post.getPostNo()));
	}

	@Transactional(readOnly = true)
	public PostResponse getPost(Long postNo) {
		return toResponse(loadExistingPost(postNo));
	}

	@Transactional(readOnly = true)
	public List<PostResponse> getPosts() {
		return postRepository.findAllPosts()
			.stream()
			.map(this::toResponse)
			.toList();
	}

	private void validateUserExists(String userId) {
		if (userRepository.countByUserId(userId) == 0) {
			throw new UserNotFoundException("User not found: " + userId);
		}
	}

	private Post loadExistingPost(Long postNo) {
		Post post = postRepository.findByPostNo(postNo);
		if (post == null) {
			throw new PostNotFoundException("Post not found: " + postNo);
		}

		return post;
	}

	private PostResponse toResponse(Post post) {
		return new PostResponse(
			post.getPostNo(),
			post.getUserId(),
			post.getTitle(),
			post.getContent(),
			post.getCreatedAt()
		);
	}
}
