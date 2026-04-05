package com.finance.dashboard.utils;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.finance.dashboard.constants.Constant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TraceIdFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String traceId = UUID.randomUUID().toString();

		MDC.put(Constant.TraceId, traceId);

		log.info("Put the Trace ID ");

		try {
			filterChain.doFilter(request, response);
		} finally {

			log.info("removed traceid to prevent from memory leaks");

			MDC.remove(Constant.TraceId); // prevent memory leak
		}
	}
}