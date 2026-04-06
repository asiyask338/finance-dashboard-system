package com.finance.dashboard.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.finance.dashboard.constants.Constant;
import com.finance.dashboard.utils.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends GenericFilter {

	private final JWTUtil jwtUtil;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;

		String authHeader = req.getHeader(Constant.AUTHORIZAION);

		if (authHeader != null && authHeader.startsWith(Constant.BEARER)) {

			String token = authHeader.substring(7);

			try {

				Long userId = jwtUtil.extractUserId(token);
				String role = jwtUtil.extractRole(token);

				Authentication auth = new UsernamePasswordAuthenticationToken(userId, null,
						List.of(new SimpleGrantedAuthority(role)));
				SecurityContextHolder.getContext().setAuthentication(auth);

			} catch (Exception e) {

				log.error("Authentication failed... : {} ", e.getMessage());
			}
		}

		chain.doFilter(request, response);
	}
}