package com.finance.dashboard.service;

import java.util.List;

import com.finance.dashboard.dto.res.CategorySummaryResponse;
import com.finance.dashboard.dto.res.DashboardSummaryResponse;

public interface DashboardService {
	DashboardSummaryResponse getSummary();

	List<CategorySummaryResponse> getCategorySummary();

}
