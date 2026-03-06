package com.example.mytree.controller;

import java.util.List;
import java.util.regex.Pattern;

import com.example.mytree.dto.LoginResponse;
import com.example.mytree.dto.UserLoginRequest;
import com.example.mytree.dto.UserResponse;
import com.example.mytree.dto.UserSignupRequest;
import com.example.mytree.dto.UserUpdateRequest;
import com.example.mytree.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private static final Pattern IPV4_PATTERN = Pattern.compile(
		"^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}$"
	);

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/signup")
	public ResponseEntity<UserResponse> signup(
		@Valid @RequestBody UserSignupRequest request,
		HttpServletRequest httpServletRequest,
		@RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor
	) {
		String clientIp = resolveIpv4Address(httpServletRequest, forwardedFor);
		UserResponse response = userService.register(request, clientIp);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
		return ResponseEntity.ok(userService.login(request));
	}

	@GetMapping("/{userId}")
	public ResponseEntity<UserResponse> getUser(@PathVariable String userId) {
		return ResponseEntity.ok(userService.getUser(userId));
	}

	@GetMapping
	public ResponseEntity<List<UserResponse>> getUsers() {
		return ResponseEntity.ok(userService.getUsers());
	}

	@PutMapping("/{userId}")
	public ResponseEntity<UserResponse> updateUser(
		@PathVariable String userId,
		@Valid @RequestBody UserUpdateRequest request
	) {
		return ResponseEntity.ok(userService.updateUser(userId, request));
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
		userService.deleteUser(userId);
		return ResponseEntity.noContent().build();
	}

	private String resolveIpv4Address(HttpServletRequest request, String forwardedFor) {
		String candidate = firstIp(forwardedFor);
		if (candidate == null || candidate.isBlank()) {
			candidate = request.getRemoteAddr();
		}

		if ("0:0:0:0:0:0:0:1".equals(candidate) || "::1".equals(candidate)) {
			candidate = "127.0.0.1";
		}

		if (candidate != null && candidate.startsWith("::ffff:")) {
			candidate = candidate.substring(7);
		}

		if (candidate == null || !IPV4_PATTERN.matcher(candidate).matches()) {
			throw new IllegalArgumentException("Client IPv4 address could not be resolved");
		}

		return candidate;
	}

	private String firstIp(String forwardedFor) {
		if (forwardedFor == null || forwardedFor.isBlank()) {
			return null;
		}

		return forwardedFor.split(",")[0].trim();
	}
}
