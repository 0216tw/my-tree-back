package com.example.mytree.exception;

public class DuplicateUserException extends RuntimeException {

	public DuplicateUserException(String message) {
		super(message);
	}
}
