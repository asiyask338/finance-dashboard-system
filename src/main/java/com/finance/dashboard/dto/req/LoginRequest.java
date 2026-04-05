package com.finance.dashboard.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

	@Email(message = "Invalid email format")
	@NotBlank(message = "Email is required")
	private String email;

	@Size(min = 4, max = 20, message = "Password must be between 4 and 20 characters")
	private String password;
}
