package com.example.mytree.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostCreateRequest(
	@NotBlank(message = "userId is required")
	@Size(max = 50, message = "userId must be 50 characters or fewer")
	String userId,

	@NotBlank(message = "title is required")
	@Size(max = 150, message = "title must be 150 characters or fewer")
	String title,

	@NotBlank(message = "content is required")
	@Size(max = 4000, message = "content must be 4000 characters or fewer")
	String content
) {
}
