package com.finance.dashboard.exception;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
	private String errorCode;
	private String message;
	private String path;
	private String traceId;

	private LocalDateTime timeStamp;

	private String httpMethod;
}