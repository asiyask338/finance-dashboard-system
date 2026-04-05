package com.finance.dashboard.service;

import java.time.LocalDate;
import java.util.List;

import com.finance.dashboard.dto.req.CreateFinancialRecordRequest;
import com.finance.dashboard.dto.req.UpdateFinancialRecordRequest;
import com.finance.dashboard.dto.res.FinancialRecordResponse;

public interface FinancialRecordService {

	FinancialRecordResponse createRecord(CreateFinancialRecordRequest request);

	List<FinancialRecordResponse> getAllRecords();

	List<FinancialRecordResponse> getByType(String type);

	List<FinancialRecordResponse> getByCategory(String category);

	void deleteRecord(Long id);

	FinancialRecordResponse updateRecord(Long id, UpdateFinancialRecordRequest request);

	List<FinancialRecordResponse> filterRecords(String type, String category, LocalDate startDate, LocalDate endDate);
}