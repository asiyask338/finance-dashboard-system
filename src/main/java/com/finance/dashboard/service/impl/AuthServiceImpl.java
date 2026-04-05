package com.finance.dashboard.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.finance.dashboard.dto.req.LoginRequest;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.exception.BadRequestException;
import com.finance.dashboard.exception.BusinessException;
import com.finance.dashboard.exception.ResourceNotFoundException;
import com.finance.dashboard.repository.UserRepository;
import com.finance.dashboard.service.AuthService;
import com.finance.dashboard.utils.JWTUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final JWTUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;

	@Override
	public String login(LoginRequest request) {

		log.info("Attempting login for email: {}", request.getEmail());

		User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> {
			log.error("User not found with email: {}", request.getEmail());

			return new ResourceNotFoundException("User not found with email: " + request.getEmail());
		});

		if (user.isDeleted()) {
			throw new BadRequestException("User is inactive or deleted");
		}
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new BusinessException("Invalid email or password");
		}

		log.info("User found with id: {} and role: {}", user.getId(), user.getRole().getName());

		String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().getName());

		log.info("JWT token generated successfully for user: {}", request.getEmail());

		return token;
	}
}
