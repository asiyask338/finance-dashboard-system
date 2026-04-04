package com.finance.dashboard.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.finance.dashboard.dto.req.CreateFinancialRecordRequest;
import com.finance.dashboard.dto.res.FinancialRecordResponse;
import com.finance.dashboard.entity.FinancialRecord;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.exception.ResourceNotFoundException;
import com.finance.dashboard.repository.FinancialRecordRepository;
import com.finance.dashboard.repository.UserRepository;
import com.finance.dashboard.service.FinancialRecordService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialRecordServiceImpl implements FinancialRecordService {

	private final FinancialRecordRepository recordRepository;
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;

	@Override
	public FinancialRecordResponse createRecord(CreateFinancialRecordRequest request) {

		log.info("Creating financial record for user ID: {}", request.getUserId());

		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		log.info("User found: {}", user.getName());

		FinancialRecord record = modelMapper.map(request, FinancialRecord.class);
		record.setUser(user);
		record.setCreatedBy(user.getId());

		FinancialRecord saved = recordRepository.save(record);

		FinancialRecordResponse response = modelMapper.map(saved, FinancialRecordResponse.class);
		response.setUserName(saved.getUser().getName());

		log.info("Financial record created with ID: {}", saved.getId());

		return response;
	}

	@Override
	public List<FinancialRecordResponse> getAllRecords() {

		log.info("Retrieving all financial records...");

		return recordRepository.findAll().stream()
				.map(record -> FinancialRecordResponse.builder().id(record.getId()).amount(record.getAmount())
						.type(record.getType()).category(record.getCategory()).recordDate(record.getRecordDate())
						.notes(record.getNotes()).userName(record.getUser().getName()).build())
				.toList();
	}

	@Override
	public List<FinancialRecordResponse> getByType(String type) {

		log.info("Retrieving financial records by type: {}", type);

		return recordRepository.findByType(type).stream()
				.map(r -> FinancialRecordResponse.builder().id(r.getId()).amount(r.getAmount()).type(r.getType())
						.category(r.getCategory()).recordDate(r.getRecordDate()).notes(r.getNotes())
						.userName(r.getUser().getName()).build())
				.toList();
	}

	@Override
	public List<FinancialRecordResponse> getByCategory(String category) {

		log.info("Retrieving financial records by category: {}", category);

		return recordRepository.findByCategory(category).stream()
				.map(r -> FinancialRecordResponse.builder().id(r.getId()).amount(r.getAmount()).type(r.getType())
						.category(r.getCategory()).recordDate(r.getRecordDate()).notes(r.getNotes())
						.userName(r.getUser().getName()).build())
				.toList();
	}

	@Override
	public void deleteRecord(Long id) {

		log.info("Deleting financial record with ID: {}", id);

		FinancialRecord record = recordRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Record not found with ID: " + id));

		log.info("Financial record found: {} with amount: {}", record.getId(), record.getAmount());

		recordRepository.delete(record);
	}
}
