package com.example.mytree.dto;

import java.time.LocalDateTime;

public record UserResponse(
	String userId,
	String name,
	LocalDateTime createdAt,
	String ipAddress
) {
}
