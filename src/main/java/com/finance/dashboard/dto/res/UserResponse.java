package com.finance.dashboard.dto.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

	private Long id;

	private String name;
	private String email;
	private String role;
	private String status;
}