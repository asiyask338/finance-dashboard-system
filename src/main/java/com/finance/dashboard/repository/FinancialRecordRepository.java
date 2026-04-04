package com.finance.dashboard.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.finance.dashboard.entity.FinancialRecord;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

	List<FinancialRecord> findByType(String type);

	List<FinancialRecord> findByCategory(String category);

	List<FinancialRecord> findByRecordDateBetween(LocalDate startDate, LocalDate endDate);

	// Custom query to calculate total income and expenses
	@Query("SELECT SUM(r.amount) FROM FinancialRecord r WHERE r.type = 'INCOME'")
	Double getTotalIncome();

	// Custom query to calculate total expenses
	@Query("SELECT SUM(r.amount) FROM FinancialRecord r WHERE r.type = 'EXPENSE'")
	Double getTotalExpense();

	// Custom query to get summary by category
	@Query("SELECT r.category, SUM(r.amount) FROM FinancialRecord r GROUP BY r.category")
	List<Object[]> getCategorySummary();
}