package com.finance.dashboard.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

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

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private ModelMapper modelMapper;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserServiceImpl userService;

	private Role role;
	private User user;

	@BeforeEach
	void setUp() {

		role = new Role();
		role.setId(1L);
		role.setName("ROLE_USER");

		user = new User();
		user.setId(10L);
		user.setName("asiya");
		user.setEmail("asiya@example.com");
		user.setRole(role);
		user.setStatus(UserStatus.ACTIVE);
		user.setDeleted(false);
	}

	@Test
	void shouldCreateUserSuccessfully() {

		CreateUserRequest request = new CreateUserRequest();
		request.setName("shaik");
		request.setEmail("shaik@example.com");
		request.setPassword("shaik");
		request.setRoleId(1L);

		User mappedUser = new User();

		User savedUser = new User();
		savedUser.setId(20L);
		savedUser.setEmail("shaik@example.com");
		savedUser.setRole(role);

		UserResponse response = new UserResponse();
		response.setId(20L);
		response.setEmail("shaik@example.com");
		response.setRole("ROLE_USER");

		when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
		when(modelMapper.map(request, User.class)).thenReturn(mappedUser);
		when(passwordEncoder.encode("shaik")).thenReturn("encoded");
		when(userRepository.save(any(User.class))).thenReturn(savedUser);
		when(modelMapper.map(savedUser, UserResponse.class)).thenReturn(response);

		UserResponse result = userService.createUser(request);

		assertNotNull(result);
		assertEquals("shaik@example.com", result.getEmail());
		assertEquals("ROLE_USER", result.getRole());

		verify(userRepository).save(any(User.class));
	}

	@Test
	void shouldThrowExceptionWhenRoleNotFound() {

		CreateUserRequest request = new CreateUserRequest();
		request.setRoleId(99L);

		when(roleRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> userService.createUser(request));
	}

	@Test
	void shouldReturnAllUsers() {

		User user2 = new User();
		user2.setId(11L);
		user2.setEmail("test@example.com");

		UserResponse r1 = new UserResponse();
		r1.setEmail("asiya@example.com");

		UserResponse r2 = new UserResponse();
		r2.setEmail("test@example.com");

		when(userRepository.findAll()).thenReturn(List.of(user, user2));
		when(modelMapper.map(user, UserResponse.class)).thenReturn(r1);
		when(modelMapper.map(user2, UserResponse.class)).thenReturn(r2);

		List<UserResponse> result = userService.getAllUsers();

		assertEquals(2, result.size());
		assertTrue(result.stream().anyMatch(r -> r.getEmail().equals("asiya@example.com")));
		assertTrue(result.stream().anyMatch(r -> r.getEmail().equals("test@example.com")));
	}

	@Test
	void shouldReturnUserById() {

		when(userRepository.findById(10L)).thenReturn(Optional.of(user));

		UserResponse response = new UserResponse();
		response.setEmail("asiya@example.com");

		when(modelMapper.map(user, UserResponse.class)).thenReturn(response);

		UserResponse result = userService.getUserById(10L);

		assertEquals("asiya@example.com", result.getEmail());
	}

	@Test
	void shouldThrowExceptionWhenUserNotFound() {

		when(userRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
	}

	@Test
	void shouldDeleteUser() {

		when(userRepository.findById(10L)).thenReturn(Optional.of(user));

		userService.deleteUser(10L);

		assertTrue(user.isDeleted());
		assertEquals(UserStatus.INACTIVE, user.getStatus());

		verify(userRepository).delete(user);
	}

	@Test
	void shouldUpdateUserSuccessfully() {

		UpdateUserRequest request = new UpdateUserRequest();
		request.setName("updated");
		request.setEmail("updated@example.com");
		request.setRoleId(1L);
		request.setStatus("INACTIVE");

		when(userRepository.findById(10L)).thenReturn(Optional.of(user));
		when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
		when(userRepository.save(any(User.class))).thenReturn(user);

		UserResponse response = new UserResponse();
		response.setEmail("updated@example.com");

		when(modelMapper.map(user, UserResponse.class)).thenReturn(response);

		UserResponse result = userService.updateUser(10L, request);

		assertEquals("updated@example.com", result.getEmail());
		assertEquals(UserStatus.INACTIVE, user.getStatus());
	}

	@Test
	void shouldThrowExceptionWhenUserDeleted() {

		user.setDeleted(true);

		when(userRepository.findById(10L)).thenReturn(Optional.of(user));

		assertThrows(BadRequestException.class, () -> userService.updateUser(10L, new UpdateUserRequest()));
	}

	@Test
	void shouldThrowExceptionForInvalidStatus() {

		UpdateUserRequest request = new UpdateUserRequest();
		request.setStatus("INVALID");
		request.setRoleId(1L);

		when(userRepository.findById(10L)).thenReturn(Optional.of(user));
		when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

		assertThrows(BadRequestException.class, () -> userService.updateUser(10L, request));
	}
}