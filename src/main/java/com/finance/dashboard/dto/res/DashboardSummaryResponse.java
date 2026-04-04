package com.finance.dashboard.dto.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardSummaryResponse {

	private Double totalIncome;
	private Double totalExpense;
	private Double netBalance;
}