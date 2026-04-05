package com.finance.dashboard.dto.res;

import lombok.Data;

@Data
public class MonthlySummaryResponse {

	private String month;

	private Double totalIncome;
	private Double totalExpense;
}
