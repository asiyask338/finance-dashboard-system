package com.finance.dashboard.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRequest {

	@NotBlank(message = "Name is required")
	@Min(value = 3, message = "Name must be at least 3 characters")
	private String name;

	@Email(message = "Invalid email")
	@NotBlank(message = "Email is required")
	private String email;

	@NotNull(message = "RoleId is required")
	private Long roleId;

	@NotNull(message = "Status is required")
	private String status; // ACTIVE / INACTIVE
}