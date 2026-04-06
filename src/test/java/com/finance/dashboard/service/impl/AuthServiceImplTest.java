package com.finance.dashboard.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.finance.dashboard.dto.req.LoginRequest;
import com.finance.dashboard.entity.Role;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.exception.BadRequestException;
import com.finance.dashboard.exception.BusinessException;
import com.finance.dashboard.exception.ResourceNotFoundException;
import com.finance.dashboard.repository.UserRepository;
import com.finance.dashboard.utils.JWTUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private JWTUtil jwtUtil;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private AuthServiceImpl service;

	@Test
	void login_success_returnsToken() {
		LoginRequest req = new LoginRequest();
		req.setEmail("asiya@example.com");
		req.setPassword("pw");

		Role role = new Role();
		role.setName("USER");

		User user = new User();
		user.setId(2L);
		user.setEmail(req.getEmail());
		user.setPassword("encoded password");
		user.setDeleted(false);
		user.setRole(role);

		when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(req.getPassword(), user.getPassword())).thenReturn(true);
		when(jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().getName())).thenReturn("token123");

		String token = service.login(req);

		assertEquals("token123", token);
	}

	@Test
	void login_userNotFound_throwsResourceNotFound() {
		LoginRequest req = new LoginRequest();
		req.setEmail("missingmail@example.com");
		req.setPassword("pw");

		when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> service.login(req));
	}

	@Test
	void login_userDeleted_throwsBadRequest() {
		LoginRequest req = new LoginRequest();
		req.setEmail("simple@example.com");
		req.setPassword("pw");

		User user = new User();
		user.setEmail(req.getEmail());
		user.setDeleted(true);

		when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(user));

		assertThrows(BadRequestException.class, () -> service.login(req));
	}

	@Test
	void login_invalidPassword_throwsBusinessException() {
		LoginRequest req = new LoginRequest();
		req.setEmail("asiya@example.com");
		req.setPassword("wrong");

		User user = new User();
		user.setEmail(req.getEmail());
		user.setPassword("encoded");
		user.setDeleted(false);

		when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(req.getPassword(), user.getPassword())).thenReturn(false);

		assertThrows(BusinessException.class, () -> service.login(req));
	}
}
