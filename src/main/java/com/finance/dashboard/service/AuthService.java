package com.finance.dashboard.service;

import com.finance.dashboard.dto.req.LoginRequest;

public interface AuthService {
	String login(LoginRequest request);
}
