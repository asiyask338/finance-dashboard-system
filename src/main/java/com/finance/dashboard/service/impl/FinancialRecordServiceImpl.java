package com.finance.dashboard.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.finance.dashboard.constants.Constant;
import com.finance.dashboard.dto.req.CreateFinancialRecordRequest;
import com.finance.dashboard.dto.req.UpdateFinancialRecordRequest;
import com.finance.dashboard.dto.res.FinancialRecordResponse;
import com.finance.dashboard.entity.FinancialRecord;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.exception.BadRequestException;
import com.finance.dashboard.exception.BusinessException;
import com.finance.dashboard.exception.DuplicateResourceException;
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

		boolean exists = recordRepository.existsByUserIdAndRecordDateAndTypeAndCategory(request.getUserId(),
				request.getRecordDate(), request.getType(), request.getCategory());

		if (exists) {
			throw new DuplicateResourceException("Duplicate financial record already exists");
		}

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

		return recordRepository.findAll().stream().map(record -> modelMapper.map(record, FinancialRecordResponse.class))
				.toList();
	}

	@Override
	public List<FinancialRecordResponse> getByType(String type) {

		log.info("Retrieving financial records by type: {}", type);

		String normalizedType = type.toUpperCase();

		if (!normalizedType.equals(Constant.INCOME) && !normalizedType.equals(Constant.EXPENSE)) {
			throw new BusinessException("Invalid record type: " + type);
		}

		return recordRepository.findByTypeIgnoreCase(normalizedType).stream()
				.map(record -> modelMapper.map(record, FinancialRecordResponse.class)).toList();
	}

	@Override
	public List<FinancialRecordResponse> getByCategory(String category) {

		log.info("Retrieving financial records by category: {}", category);

		return recordRepository.findByCategoryIgnoreCase(category).stream()
				.map(record -> modelMapper.map(record, FinancialRecordResponse.class)).toList();
	}

	@Override
	public void deleteRecord(Long id) {

		log.info("Deleting financial record with ID: {}", id);

		FinancialRecord record = recordRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Record not found with ID: " + id));

		log.info("Financial record found: {} with amount: {}", record.getId(), record.getAmount());

		recordRepository.delete(record);
	}

	@Override
	public FinancialRecordResponse updateRecord(Long id, UpdateFinancialRecordRequest request) {

		log.info("Updating financial record with ID: {}", id);

		FinancialRecord record = recordRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Record not found"));

		log.info("Financial record found: {} with amount: {}", record.getId(), record.getAmount());

		record.setAmount(request.getAmount());
		record.setType(request.getType());
		record.setCategory(request.getCategory());
		record.setRecordDate(request.getRecordDate());
		record.setNotes(request.getNotes());

		FinancialRecord updated = recordRepository.save(record);

		if (!request.getType().equals(Constant.INCOME) && !request.getType().equals(Constant.EXPENSE)) {
			log.error("Invalid record type: {}", request.getType());

			throw new BadRequestException("Invalid type");
		}

		FinancialRecordResponse response = modelMapper.map(updated, FinancialRecordResponse.class);
		response.setUserName(updated.getUser().getName());

		log.info("Financial record updated with ID: {}", updated.getId());

		return response;
	}

	@Override
	public List<FinancialRecordResponse> filterRecords(String type, String category, LocalDate startDate,
			LocalDate endDate) {

		log.info("Filtering financial records with type: {}, category: {}, startDate: {}, endDate: {}", type, category,
				startDate, endDate);

		List<FinancialRecord> records = recordRepository.filterRecords(type, category, startDate, endDate);

		log.info("Found {} records matching filter criteria", records.size());

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
}
