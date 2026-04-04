package com.finance.dashboard.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finance.dashboard.dto.res.CategorySummaryResponse;
import com.finance.dashboard.dto.res.DashboardSummaryResponse;
import com.finance.dashboard.service.DashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dashboard APIs", description = "Analytics and summary endpoints")
public class DashboardController {

	private final DashboardService dashboardService;

	@GetMapping("/summary")
	@Operation(summary = "Get dashboard summary", description = "Provides an overview of financial data including total income, expenses, and net balance.")
	public DashboardSummaryResponse getSummary() {

		log.info("Received request for dashboard summary");

		DashboardSummaryResponse response = dashboardService.getSummary();

		log.info("Dashboard summary response: {}", response);

		return response;
	}

	@GetMapping("/category")
	@Operation(summary = "Get category summary", description = "Provides a breakdown of income and expenses by category.")
	public List<CategorySummaryResponse> getCategorySummary() {

		log.info("Received request for category summary");

		List<CategorySummaryResponse> response = dashboardService.getCategorySummary();

		log.info("Category summary response: {}", response);

		return response;
	}
}