package com.finance.dashboard.dto.res;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialRecordResponse {

	private Long id;
	private Double amount;

	private String type;
	private String category;
	private String notes;
	private String userName;

	private LocalDate recordDate;
}
