
package com.finance.dashboard.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.finance.dashboard.dto.req.CreateUserRequest;
import com.finance.dashboard.dto.req.UpdateUserRequest;
import com.finance.dashboard.dto.res.UserResponse;
import com.finance.dashboard.entity.Role;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.enums.UserStatus;
import com.finance.dashboard.exception.BadRequestException;
import com.finance.dashboard.exception.ResourceNotFoundException;
import com.finance.dashboard.repository.RoleRepository;
import com.finance.dashboard.repository.UserRepository;
import com.finance.dashboard.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final ModelMapper modelMapper;

	@Override
	public UserResponse createUser(CreateUserRequest request) {

//		checkAdminAccess(currentUserRole);

		log.info("Creating user with email: {}", request.getEmail());

		Role role = roleRepository.findById(request.getRoleId())
				.orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + request.getRoleId()));

		log.info("Found role: {}", role.getName());

		User user = modelMapper.map(request, User.class);
		user.setRole(role);
		user.setStatus(UserStatus.ACTIVE);

		User savedUser = userRepository.save(user);

		UserResponse response = modelMapper.map(savedUser, UserResponse.class);
		response.setRole(savedUser.getRole().getName());

		log.info("User created with id: {}", savedUser.getId());

		return response;

	}

	@Override
	public List<UserResponse> getAllUsers() {
		log.info("Fetching all users");

		return userRepository.findAll().stream().map(user -> modelMapper.map(user, UserResponse.class)).toList();
	}

	@Override
	public UserResponse getUserById(Long id) {

		log.info("Fetching user with id: {}", id);

		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

		log.info("Found user: {}", user.getEmail());

		return modelMapper.map(user, UserResponse.class);

	}

	@Override
	public void deleteUser(Long id) {

		log.info("Deleting user with id: {}", id);

		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

		log.info("Found user: {}", user.getEmail());

		userRepository.delete(user);

		log.info("User deleted with id: {}", id);
	}

	@Override
	public UserResponse updateUser(Long id, UpdateUserRequest request) {

		log.info("Updating user with id: {}", id);

		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		log.info("Found user: {}", user.getEmail());

		Role role = roleRepository.findById(request.getRoleId())
				.orElseThrow(() -> new ResourceNotFoundException("Role not found"));

		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setRole(role);

		try {
			user.setStatus(UserStatus.valueOf(request.getStatus()));

			log.info("Updated user status to: {}", user.getStatus());
		} catch (IllegalArgumentException ex) {
			log.error("Invalid status value: {}", request.getStatus());

			throw new BadRequestException("Invalid status value");
		}

		User updatedUser = userRepository.save(user);

		log.info("User updated with id: {}", updatedUser.getId());

		return modelMapper.map(user, UserResponse.class);
	}
}