package com.finance.dashboard.security;

import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.finance.dashboard.constants.Constant;

public class JwtAuthentication extends AbstractAuthenticationToken {

	private final Long userId;
	private final String role;

	public JwtAuthentication(Long userId, String role) {
		super(List.of(new SimpleGrantedAuthority(Constant.ACCESSROLE + role)));
		this.userId = userId;
		this.role = role;
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return userId;
	}

	public Long getUserId() {
		return userId;
	}
}