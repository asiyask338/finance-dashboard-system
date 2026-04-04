package com.finance.dashboard.dto.req;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateFinancialRecordRequest {

	@NotNull(message = "Amount is required")
	@Positive(message = "Amount must be greater than 0")
	private Double amount;

	@NotBlank(message = "Type is required")
	@Pattern(regexp = "INCOME|EXPENSE", message = "Type must be INCOME or EXPENSE")
	private String type;

	@Size(max = 100, message = "Category cannot exceed 100 characters")
	private String category;

	@NotNull(message = "Record date is required")
	private LocalDate recordDate;

	@Size(max = 255, message = "Notes cannot exceed 255 characters")
	private String notes;

	@NotNull(message = "UserId is required")
	private Long userId;
}