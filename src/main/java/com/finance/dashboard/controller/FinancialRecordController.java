package com.finance.dashboard.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finance.dashboard.dto.req.CreateFinancialRecordRequest;
import com.finance.dashboard.dto.res.FinancialRecordResponse;
import com.finance.dashboard.service.FinancialRecordService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Financial Record APIs", description = "Manage income and expense records")
public class FinancialRecordController {

	private final FinancialRecordService recordService;

	@PostMapping
	@Operation(summary = "Create a new financial record", description = "Add a new income or expense record to the system")
	public FinancialRecordResponse createRecord(@Valid @RequestBody CreateFinancialRecordRequest request) {

		log.info("Creating financial record: {}", request);

		FinancialRecordResponse response = recordService.createRecord(request);

		log.info("Financial record created successfully: {}", response);

		return response;
	}

	@GetMapping
	@Operation(summary = "Get all financial records", description = "Retrieve a list of all income and expense records")
	public List<FinancialRecordResponse> getAllRecords() {

		log.info("Fetching all financial records");

		List<FinancialRecordResponse> response = recordService.getAllRecords();

		log.info("Fetched {} financial records", response.size());

		return response;
	}

	@GetMapping("/type")
	@Operation(summary = "Get financial records by type", description = "Retrieve a list of financial records filtered by type (income or expense)")
	public List<FinancialRecordResponse> getByType(@RequestParam String type) {

		log.info("Fetching financial records by type: {}", type);

		List<FinancialRecordResponse> response = recordService.getByType(type);

		log.info("Fetched {} financial records of type {}", response.size(), type);

		return response;
	}

	@GetMapping("/category")
	@Operation(summary = "Get financial records by category", description = "Retrieve a list of financial records filtered by category")
	public List<FinancialRecordResponse> getByCategory(@RequestParam String category) {

		log.info("Fetching financial records by category: {}", category);

		List<FinancialRecordResponse> response = recordService.getByCategory(category);

		log.info("Fetched {} financial records of category {}", response.size(), category);

		return response;
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a financial record", description = "Remove a financial record by its ID")
	public ResponseEntity<String> deleteRecord(@PathVariable Long id) {

		log.info("Deleting financial record with id: {}", id);

		recordService.deleteRecord(id);

		log.info("Financial record with id {} deleted successfully", id);

		return ResponseEntity.ok("Financial record deleted successfully");
	}
}