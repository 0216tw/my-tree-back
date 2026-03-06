package com.example.mytree.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserSignupRequest(
	@NotBlank(message = "userId is required")
	@Size(max = 50, message = "userId must be 50 characters or fewer")
	String userId,

	@NotBlank(message = "password is required")
	@Size(max = 255, message = "password must be 255 characters or fewer")
	String password,

	@NotBlank(message = "name is required")
	@Size(max = 50, message = "name must be 50 characters or fewer")
	String name
) {
}
