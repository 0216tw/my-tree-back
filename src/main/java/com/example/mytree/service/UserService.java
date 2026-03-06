package com.example.mytree.service;

import java.util.List;

import com.example.mytree.domain.User;
import com.example.mytree.dto.LoginResponse;
import com.example.mytree.dto.UserLoginRequest;
import com.example.mytree.dto.UserResponse;
import com.example.mytree.dto.UserSignupRequest;
import com.example.mytree.dto.UserUpdateRequest;
import com.example.mytree.exception.AuthenticationFailedException;
import com.example.mytree.exception.DuplicateUserException;
import com.example.mytree.exception.UserNotFoundException;
import com.example.mytree.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional
	public UserResponse register(UserSignupRequest request, String ipAddress) {
		if (userRepository.countByUserId(request.userId()) > 0) {
			throw new DuplicateUserException("User ID already exists: " + request.userId());
		}

		User user = new User();
		user.setUserId(request.userId());
		user.setPassword(request.password());
		user.setName(request.name());
		user.setIpAddress(ipAddress);

		userRepository.insertUser(user);

		return toResponse(loadExistingUser(request.userId()));
	}

	@Transactional(readOnly = true)
	public LoginResponse login(UserLoginRequest request) {
		User user = loadExistingUser(request.userId());
		if (!request.password().equals(user.getPassword())) {
			throw new AuthenticationFailedException("Invalid userId or password");
		}

		return new LoginResponse(user.getUserId(), user.getName(), true);
	}

	@Transactional(readOnly = true)
	public UserResponse getUser(String userId) {
		return toResponse(loadExistingUser(userId));
	}

	@Transactional(readOnly = true)
	public List<UserResponse> getUsers() {
		return userRepository.findAllUsers()
			.stream()
			.map(this::toResponse)
			.toList();
	}

	@Transactional
	public UserResponse updateUser(String userId, UserUpdateRequest request) {
		User existingUser = loadExistingUser(userId);
		existingUser.setPassword(request.password());
		existingUser.setName(request.name());

		userRepository.updateUser(existingUser);

		return toResponse(loadExistingUser(userId));
	}

	@Transactional
	public void deleteUser(String userId) {
		loadExistingUser(userId);
		userRepository.deleteByUserId(userId);
	}

	private User loadExistingUser(String userId) {
		User user = userRepository.findByUserId(userId);
		if (user == null) {
			throw new UserNotFoundException("User not found: " + userId);
		}

		return user;
	}

	private UserResponse toResponse(User user) {
		return new UserResponse(
			user.getUserId(),
			user.getName(),
			user.getCreatedAt(),
			user.getIpAddress()
		);
	}
}
