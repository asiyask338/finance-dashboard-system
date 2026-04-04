package com.finance.dashboard.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finance.dashboard.dto.req.CreateUserRequest;
import com.finance.dashboard.dto.req.UpdateUserRequest;
import com.finance.dashboard.dto.res.UserResponse;
import com.finance.dashboard.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User APIs", description = "User management operations")
public class UserController {

	private final UserService userService;

	@PostMapping
	@Operation(summary = "Create User", description = "Create a new user with role")
	public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {

		log.info("Received request to create user: {}", request);

		UserResponse response = userService.createUser(request);

		log.info("User created successfully: {}", response);

		return response;
	}

	@GetMapping
	@Operation(summary = "Get All Users", description = "Retrieve a list of all users")
	public List<UserResponse> getAllUsers() {

		log.info("Received request to get all users");

		List<UserResponse> response = userService.getAllUsers();

		log.info("Returning {} users", response.size());

		return response;
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get User by ID", description = "Retrieve user details by user ID")
	public UserResponse getUser(@PathVariable Long id) {

		log.info("Received request to get user with id: {}", id);

		UserResponse response = userService.getUserById(id);

		log.info("Returning user: {}", response);

		return response;
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete User", description = "Delete a user by user ID")
	public ResponseEntity<String> deleteUser(@PathVariable Long id) {

		log.info("Received request to delete user with id: {}", id);

		userService.deleteUser(id);

		log.info("User with id {} deleted successfully", id);

		return ResponseEntity.ok("User deleted successfully");
	}

	@PutMapping("/{id}")
	@Tag(name = "Update User", description = "Update user details and role by user ID")
	public UserResponse updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {

		log.info("Received request to update user with id: {} and data: {}", id, request);

		UserResponse response = userService.updateUser(id, request);

		log.info("User with id {} updated successfully: {}", id, response);

		return response;
	}
}