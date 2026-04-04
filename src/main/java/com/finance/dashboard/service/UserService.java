package com.finance.dashboard.service;

import java.util.List;

import com.finance.dashboard.dto.req.CreateUserRequest;
import com.finance.dashboard.dto.req.UpdateUserRequest;
import com.finance.dashboard.dto.res.UserResponse;

public interface UserService {

	UserResponse createUser(CreateUserRequest request);

	List<UserResponse> getAllUsers();

	UserResponse getUserById(Long id);

	void deleteUser(Long id);

	UserResponse updateUser(Long id, UpdateUserRequest request);
}