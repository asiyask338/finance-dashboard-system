package com.finance.dashboard.utils;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.finance.dashboard.constants.Constant;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWTUtil {

	private final Key key;
	private final long jwtExpiration;

	public JWTUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long jwtExpiration) {

		this.key = Keys.hmacShaKeyFor(secret.getBytes());
		this.jwtExpiration = jwtExpiration;
	}

	public String generateToken(Long userId, String email, String role) {

		log.info("Generating Token");
		return Jwts.builder().setSubject(email).claim(Constant.USERID, userId).claim(Constant.ROLE, role)
				.setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
				.signWith(key, SignatureAlgorithm.HS256).compact();
	}

	public Claims extractClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}

	public String extractRole(String token) {
		return extractClaims(token).get(Constant.ROLE, String.class);
	}

	public String extractEmail(String token) {
		return extractClaims(token).getSubject();
	}

	public Long extractUserId(String token) {
		return extractClaims(token).get(Constant.USERID, Long.class);
	}
}