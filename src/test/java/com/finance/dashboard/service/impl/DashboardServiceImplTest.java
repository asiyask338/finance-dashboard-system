package com.finance.dashboard.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.finance.dashboard.constants.Constant;
import com.finance.dashboard.dto.res.CategorySummaryResponse;
import com.finance.dashboard.dto.res.DashboardSummaryResponse;
import com.finance.dashboard.dto.res.FinancialRecordResponse;
import com.finance.dashboard.dto.res.MonthlySummaryResponse;
import com.finance.dashboard.entity.FinancialRecord;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.repository.FinancialRecordRepository;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

	@Mock
	private FinancialRecordRepository repository;

	@InjectMocks
	private DashboardServiceImpl dashboardService;

	@Test
	void shouldReturnZeroWhenIncomeAndExpenseAreNull() {

		when(repository.getTotalIncome()).thenReturn(null);
		when(repository.getTotalExpense()).thenReturn(null);

		DashboardSummaryResponse response = dashboardService.getSummary();

		assertEquals(0.0, response.getTotalIncome());
		assertEquals(0.0, response.getTotalExpense());
		assertEquals(0.0, response.getNetBalance());
	}

	@Test
	void shouldMapCategorySummaryCorrectly() {

		Object[] data = new Object[] { "Food", 123.45 };

		when(repository.getCategorySummary()).thenReturn(List.<Object[]>of(data));
		List<CategorySummaryResponse> response = dashboardService.getCategorySummary();

		assertEquals(1, response.size());
		assertEquals("Food", response.get(0).getCategory());
		assertEquals(123.45, response.get(0).getTotal());
	}

	@Test
	void shouldMapRecentRecordsCorrectly() {

		FinancialRecord record = new FinancialRecord();
		record.setId(5L);
		record.setAmount(50.0);
		record.setType(Constant.EXPENSE);
		record.setCategory(Constant.BONUS);

		User user = new User();
		user.setName("Asiya Shaik");
		record.setUser(user);

		when(repository.findTop5ByOrderByRecordDateDesc()).thenReturn(List.of(record));

		List<FinancialRecordResponse> response = dashboardService.getRecentRecords();

		assertEquals(1, response.size());
		assertEquals(5L, response.get(0).getId());
		assertEquals("Asiya Shaik", response.get(0).getUserName());
	}

	@Test
	void shouldHandleNullValuesInMonthlySummary() {

		Object[] data = new Object[] { 1, null, 200.0 };

		when(repository.getMonthlySummary()).thenReturn(List.<Object[]>of(data));

		List<MonthlySummaryResponse> response = dashboardService.getMonthlySummary();

		assertEquals(1, response.size());
		assertEquals("JANUARY", response.get(0).getMonth());
		assertEquals(0.0, response.get(0).getTotalIncome());
		assertEquals(200.0, response.get(0).getTotalExpense());
	}
}
