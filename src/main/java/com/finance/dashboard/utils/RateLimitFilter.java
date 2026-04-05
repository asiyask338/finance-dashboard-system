package com.finance.dashboard.utils;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RateLimitFilter implements Filter {

	private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

	private Bucket createNewBucket() {
		Bandwidth limit = Bandwidth.simple(10, Duration.ofMinutes(1)); // 10 req/min
		return Bucket.builder().addLimit(limit).build();
	}

	private Bucket resolveBucket(String ip) {
		return cache.computeIfAbsent(ip, k -> createNewBucket());
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String ip = req.getRemoteAddr();

		Bucket bucket = resolveBucket(ip);

		log.info("Implementing rate limiting to restrict no.of requests made..");

		if (bucket.tryConsume(1)) {
			chain.doFilter(request, response);
		} else {
			res.setStatus(429);
			res.getWriter().write("Too many requests. Please try again later.");
		}
	}
}