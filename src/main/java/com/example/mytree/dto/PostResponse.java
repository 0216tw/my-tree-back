package com.example.mytree.dto;

import java.time.LocalDateTime;

public record PostResponse(
	Long postNo,
	String userId,
	String title,
	String content,
	LocalDateTime createdAt
) {
}
