package com.finance.dashboard.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.finance.dashboard.dto.res.CategorySummaryResponse;
import com.finance.dashboard.dto.res.DashboardSummaryResponse;
import com.finance.dashboard.repository.FinancialRecordRepository;
import com.finance.dashboard.service.DashboardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

	private final FinancialRecordRepository recordRepository;

	@Override
	public DashboardSummaryResponse getSummary() {

		log.info("Calculating dashboard summary...");

		Double income = recordRepository.getTotalIncome();
		Double expense = recordRepository.getTotalExpense();

		if (income == null) {
			log.warn("Total income is null, defaulting to 0.0");

			income = 0.0;
		}
		if (expense == null) {
			log.warn("Total expense is null, defaulting to 0.0");

			expense = 0.0;
		}

		log.info("Total income: {}, Total expense: {}, Net balance: {}", income, expense, income - expense);

		return DashboardSummaryResponse.builder().totalIncome(income).totalExpense(expense).netBalance(income - expense)
				.build();
	}

	@Override
	public List<CategorySummaryResponse> getCategorySummary() {

		log.info("Calculating category summary...");

		return recordRepository.getCategorySummary().stream()
				.map(obj -> CategorySummaryResponse.builder().category((String) obj[0]).total((Double) obj[1]).build())
				.toList();
	}
}