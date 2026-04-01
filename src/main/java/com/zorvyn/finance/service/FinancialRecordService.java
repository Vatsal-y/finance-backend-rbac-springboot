package com.zorvyn.finance.service;

import com.zorvyn.finance.dto.RecordRequest;
import com.zorvyn.finance.dto.RecordResponse;
import com.zorvyn.finance.exception.ResourceNotFoundException;
import com.zorvyn.finance.model.FinancialRecord;
import com.zorvyn.finance.model.TransactionType;
import com.zorvyn.finance.model.User;
import com.zorvyn.finance.repository.FinancialRecordRepository;
import com.zorvyn.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialRecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;

    @Transactional
    public RecordResponse createRecord(RecordRequest request) {
        User currentUser = getCurrentUser();

        FinancialRecord record = FinancialRecord.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .date(request.getDate())
                .note(request.getNote())
                .user(currentUser)
                .build();

        FinancialRecord saved = recordRepository.save(record);
        log.info("Record {} created by user {} — {} {} in category '{}'",
                saved.getId(), currentUser.getEmail(), saved.getType(), saved.getAmount(), saved.getCategory());
        return mapToResponse(saved);
    }

    public Page<RecordResponse> getRecords(TransactionType type, String category,
                                            LocalDate startDate, LocalDate endDate,
                                            int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FinancialRecord> records = recordRepository.findWithFilters(type, category, startDate, endDate, pageable);
        return records.map(this::mapToResponse);
    }

    public RecordResponse getRecordById(UUID id) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record not found with id: " + id));
        return mapToResponse(record);
    }

    @Transactional
    public RecordResponse updateRecord(UUID id, RecordRequest request) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record not found with id: " + id));

        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setNote(request.getNote());

        FinancialRecord saved = recordRepository.save(record);
        log.info("Record {} updated by user {}", saved.getId(), getCurrentUser().getEmail());
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteRecord(UUID id) {
        if (!recordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Financial record not found with id: " + id);
        }
        recordRepository.deleteById(id);
        log.info("Record {} deleted by user {}", id, getCurrentUser().getEmail());
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    private RecordResponse mapToResponse(FinancialRecord record) {
        return RecordResponse.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .type(record.getType().name())
                .category(record.getCategory())
                .date(record.getDate())
                .note(record.getNote())
                .userId(record.getUser().getId())
                .userName(record.getUser().getName())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
