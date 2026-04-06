package com.finance.dashboard.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

@ExtendWith(MockitoExtension.class)
class FinancialRecordServiceImplTest {

	@Mock
	private FinancialRecordRepository recordRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ModelMapper modelMapper;

	@InjectMocks
	private FinancialRecordServiceImpl service;

	private User user;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setId(1L);
		user.setName("User Name");
		user.setDeleted(false);
	}

	@Test
	void createRecord_success() {
		CreateFinancialRecordRequest req = new CreateFinancialRecordRequest();
		req.setUserId(1L);
		req.setAmount(100.0);
		req.setType(Constant.INCOME);
		req.setCategory(Constant.SALARY);
		req.setRecordDate(LocalDate.now());

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		FinancialRecord mappedRecord = new FinancialRecord();
		when(modelMapper.map(req, FinancialRecord.class)).thenReturn(mappedRecord);

		when(recordRepository.existsByUserIdAndRecordDateAndTypeAndCategory(eq(1L), any(), eq(req.getType()),
				eq(req.getCategory()))).thenReturn(false);

		FinancialRecord saved = new FinancialRecord();
		saved.setId(10L);
		saved.setAmount(req.getAmount());
		saved.setType(req.getType());
		saved.setCategory(req.getCategory());
		saved.setRecordDate(req.getRecordDate());
		saved.setUser(user);

		when(recordRepository.save(mappedRecord)).thenReturn(saved);

		FinancialRecordResponse response = new FinancialRecordResponse();
		response.setId(saved.getId());
		response.setUserName(user.getName());

		when(modelMapper.map(saved, FinancialRecordResponse.class)).thenReturn(response);

		FinancialRecordResponse result = service.createRecord(req);

		assertEquals(saved.getId(), result.getId());
		assertEquals(user.getName(), result.getUserName());
	}

	@Test
	void createRecord_userNotFound_throwsResourceNotFound() {
		CreateFinancialRecordRequest req = new CreateFinancialRecordRequest();
		req.setUserId(2L);

		when(userRepository.findById(2L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> service.createRecord(req));
	}

	@Test
	void createRecord_userDeleted_throwsBadRequest() {
		CreateFinancialRecordRequest req = new CreateFinancialRecordRequest();
		req.setUserId(1L);

		User deletedUser = new User();
		deletedUser.setId(1L);
		deletedUser.setDeleted(true);

		when(userRepository.findById(1L)).thenReturn(Optional.of(deletedUser));

		assertThrows(BadRequestException.class, () -> service.createRecord(req));
	}

	@Test
	void createRecord_duplicate_throwsDuplicateResource() {
		CreateFinancialRecordRequest req = new CreateFinancialRecordRequest();
		req.setUserId(1L);
		req.setRecordDate(LocalDate.now());
		req.setType(Constant.INCOME);
		req.setCategory(Constant.SALARY);

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		when(recordRepository.existsByUserIdAndRecordDateAndTypeAndCategory(eq(1L), any(), eq(req.getType()),
				eq(req.getCategory()))).thenReturn(true);

		assertThrows(DuplicateResourceException.class, () -> service.createRecord(req));
	}

	@Test
	void getByType_invalidType_throwsBusinessException() {
		assertThrows(BusinessException.class, () -> service.getByType("UNKNOWN"));
	}

	@Test
	void updateRecord_invalidType_throwsBadRequest() {
		Long id = 5L;
		FinancialRecord existing = new FinancialRecord();
		existing.setId(id);
		existing.setType(Constant.INCOME);
		existing.setUser(user);

		when(recordRepository.findById(id)).thenReturn(Optional.of(existing));

		UpdateFinancialRecordRequest req = new UpdateFinancialRecordRequest();
		req.setAmount(50.0);
		req.setType(Constant.INVALID_TYPE);
		req.setCategory("Misc");
		req.setRecordDate(LocalDate.now());
		req.setNotes("my notes");

		when(recordRepository.save(existing)).thenReturn(existing);

		assertThrows(BadRequestException.class, () -> service.updateRecord(id, req));
	}

	@Test
	void deleteRecord_notFound_throwsResourceNotFound() {
		when(recordRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> service.deleteRecord(99L));
	}

	@Test
	void getAllRecords_mapsPageCorrectly() {
		FinancialRecord rec = new FinancialRecord();
		rec.setId(1L);
		rec.setAmount(200.0);
		rec.setType(Constant.EXPENSE);
		rec.setCategory("Food");
		rec.setRecordDate(LocalDate.now());
		rec.setUser(user);

		Page<FinancialRecord> page = new PageImpl<>(java.util.List.of(rec));
		Pageable pageable = PageRequest.of(0, 10);

		when(recordRepository.findAll(any(Pageable.class))).thenReturn(page);

		Page<FinancialRecordResponse> result = service.getAllRecords(pageable);

		assertEquals(1, result.getTotalElements());
		FinancialRecordResponse resp = result.getContent().get(0);
		assertEquals(rec.getId(), resp.getId());
		assertEquals(user.getName(), resp.getUserName());
	}
}
