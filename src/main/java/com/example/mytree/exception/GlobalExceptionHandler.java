package com.example.mytree.exception;

import java.time.LocalDateTime;

import com.example.mytree.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DuplicateUserException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateUser(DuplicateUserException exception) {
		return buildResponse(HttpStatus.CONFLICT, exception.getMessage());
	}

	@ExceptionHandler(AuthenticationFailedException.class)
	public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationFailedException exception) {
		return buildResponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException exception) {
		return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
		return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
		String message = exception.getBindingResult()
			.getFieldErrors()
			.stream()
			.findFirst()
			.map(fieldError -> fieldError.getDefaultMessage())
			.orElse("Invalid request");

		return buildResponse(HttpStatus.BAD_REQUEST, message);
	}

	private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
		return ResponseEntity.status(status)
			.body(new ErrorResponse(status.value(), message, LocalDateTime.now()));
	}
}
