package com.finance.dashboard.service;

import java.util.List;

import com.finance.dashboard.dto.res.CategorySummaryResponse;
import com.finance.dashboard.dto.res.DashboardSummaryResponse;
import com.finance.dashboard.dto.res.FinancialRecordResponse;
import com.finance.dashboard.dto.res.MonthlySummaryResponse;

public interface DashboardService {
	DashboardSummaryResponse getSummary();

	List<CategorySummaryResponse> getCategorySummary();

	List<FinancialRecordResponse> getRecentRecords();

	List<MonthlySummaryResponse> getMonthlySummary();
}
