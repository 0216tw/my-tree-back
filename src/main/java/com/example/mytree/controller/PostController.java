package com.example.mytree.controller;

import java.util.List;

import com.example.mytree.dto.PostCreateRequest;
import com.example.mytree.dto.PostResponse;
import com.example.mytree.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostController {

	private final PostService postService;

	public PostController(PostService postService) {
		this.postService = postService;
	}

	@PostMapping
	public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostCreateRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(request));
	}

	@GetMapping("/{postNo}")
	public ResponseEntity<PostResponse> getPost(@PathVariable Long postNo) {
		return ResponseEntity.ok(postService.getPost(postNo));
	}

	@GetMapping
	public ResponseEntity<List<PostResponse>> getPosts() {
		return ResponseEntity.ok(postService.getPosts());
	}
}
