package com.finance.dashboard.exception.handler;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.finance.dashboard.constants.Constant;
import com.finance.dashboard.exception.BadRequestException;
import com.finance.dashboard.exception.DuplicateResourceException;
import com.finance.dashboard.exception.ErrorResponse;
import com.finance.dashboard.exception.ResourceNotFoundException;
import com.finance.dashboard.exception.UnauthorizedException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	// traceId
	private String generateTraceId() {

		log.info("Generating traceId for error response");

		return MDC.get(Constant.TraceId);
	}

	// resource Not Found
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {

		log.error("Resource Not Found Exception occured : {} ", ex.getMessage(), ex);

		return ErrorResponse.builder().errorCode(Constant.RESOURCE_NOT_FOUND).message(ex.getMessage())
				.path(request.getRequestURI()).httpMethod(request.getMethod()).traceId(generateTraceId())
				.timeStamp(LocalDateTime.now()).build();
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {

		log.error("Validation Exception occurred: {}", ex.getMessage(), ex);

		String errorMessage = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage()).findFirst()
				.orElse("Validation failed");

		return ErrorResponse.builder().errorCode(Constant.VALIDATION_ERROR).message(errorMessage)
				.path(request.getRequestURI()).httpMethod(request.getMethod()).traceId(generateTraceId())
				.timeStamp(LocalDateTime.now()).build();
	}

	// Validation Errors
	@ExceptionHandler(BadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleBadRequest(BadRequestException ex, HttpServletRequest request) {
		log.error("Bad Request Exception occured : {} ", ex.getMessage(), ex);

		return buildError(Constant.BAD_REQUEST, ex.getMessage(), request, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {

		log.error("Data Integrity Violation: {}", ex.getMessage(), ex);

		String message = "Duplicate resource found";

		if (ex.getMessage() != null && ex.getMessage().contains("users.email")) {
			message = "Email already exists";
		}

		return ErrorResponse.builder().errorCode(Constant.DATA_CONFLICT).message(message).path(request.getRequestURI())
				.httpMethod(request.getMethod()).traceId(generateTraceId()).timeStamp(LocalDateTime.now()).build();
	}

	@ExceptionHandler(DuplicateResourceException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleDuplicate(DuplicateResourceException ex, HttpServletRequest request) {

		log.error("Duplicate Resource Exception occured : {} ", ex.getMessage(), ex);

		return buildError(Constant.DUPLICATE_RESOURCE, ex.getMessage(), request, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(UnauthorizedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ErrorResponse handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {

		log.error("Unauthorized Exception occured : {} ", ex.getMessage(), ex);

		return buildError(Constant.UNAUTHORIZED, ex.getMessage(), request, HttpStatus.UNAUTHORIZED);
	}

	private ErrorResponse buildError(String code, String message, HttpServletRequest request, HttpStatus status) {

		log.error("{} Exception occured : {} ", status, message);

		return ErrorResponse.builder().errorCode(code).message(message).path(request.getRequestURI())
				.httpMethod(request.getMethod()).traceId(UUID.randomUUID().toString()).timeStamp(LocalDateTime.now())
				.build();
	}

	// Generic Exception
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleGeneric(Exception ex, HttpServletRequest request) {

		log.error("Internal Server Error occured : {} ", ex.getMessage(), ex);

		return ErrorResponse.builder().errorCode(Constant.INTERNAL_SERVER_ERROR).message(ex.getMessage())
				.path(request.getRequestURI()).httpMethod(request.getMethod()).traceId(generateTraceId())
				.timeStamp(LocalDateTime.now()).build();
	}
}