package com.finance.dashboard.dto.res;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FinancialRecordResponse {

	private Long id;
	private Double amount;

	private String type;
	private String category;
	private String notes;
	private String userName;

	private LocalDate recordDate;
}
