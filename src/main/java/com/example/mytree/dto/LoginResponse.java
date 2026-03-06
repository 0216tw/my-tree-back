package com.example.mytree.dto;

public record LoginResponse(
	String userId,
	String name,
	boolean authenticated
) {
}
