package com.finance.dashboard.service.impl;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.finance.dashboard.dto.res.CategorySummaryResponse;
import com.finance.dashboard.dto.res.DashboardSummaryResponse;
import com.finance.dashboard.dto.res.FinancialRecordResponse;
import com.finance.dashboard.dto.res.MonthlySummaryResponse;
import com.finance.dashboard.entity.FinancialRecord;
import com.finance.dashboard.repository.FinancialRecordRepository;
import com.finance.dashboard.service.DashboardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

	private final FinancialRecordRepository recordRepository;

	@Cacheable("summaryCache")
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

	@Cacheable("categoryCache")
	@Override
	public List<CategorySummaryResponse> getCategorySummary() {

		log.info("Calculating category summary...");

		return recordRepository.getCategorySummary().stream()
				.map(obj -> CategorySummaryResponse.builder().category((String) obj[0]).total((Double) obj[1]).build())
				.toList();
	}

	@Cacheable("recentCache")
	@Override
	public List<FinancialRecordResponse> getRecentRecords() {

		List<FinancialRecord> records = recordRepository.findTop5ByOrderByRecordDateDesc();

		return records.stream().map(record -> {
			FinancialRecordResponse response = new FinancialRecordResponse();

			response.setId(record.getId());
			response.setAmount(record.getAmount());
			response.setType(record.getType());
			response.setCategory(record.getCategory());
			response.setRecordDate(record.getRecordDate());
			response.setNotes(record.getNotes());
			response.setUserName(record.getUser().getName());

			return response;
		}).toList();
	}

	@Cacheable("monthlyCache")
	@Override
	public List<MonthlySummaryResponse> getMonthlySummary() {

		List<Object[]> results = recordRepository.getMonthlySummary();

		return results.stream().map(obj -> {

			MonthlySummaryResponse response = new MonthlySummaryResponse();

			Integer monthNumber = (Integer) obj[0];
			Double income = (Double) obj[1];
			Double expense = (Double) obj[2];

			response.setMonth(getMonthName(monthNumber));
			response.setTotalIncome(income != null ? income : 0.0);
			response.setTotalExpense(expense != null ? expense : 0.0);

			return response;

		}).toList();
	}

	private String getMonthName(int month) {
		return java.time.Month.of(month).name(); // JANUARY, FEBRUARY...
	}
}