package com.zorvyn.finance.controller;

import com.zorvyn.finance.dto.ApiResponse;
import com.zorvyn.finance.dto.RecordRequest;
import com.zorvyn.finance.dto.RecordResponse;
import com.zorvyn.finance.model.TransactionType;
import com.zorvyn.finance.service.FinancialRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
@Tag(name = "Records", description = "Financial records CRUD operations")
public class FinancialRecordController {

    private final FinancialRecordService recordService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create a financial record", description = "Creates a new income or expense record (ADMIN only)")
    public ResponseEntity<ApiResponse<RecordResponse>> createRecord(@Valid @RequestBody RecordRequest request) {
        RecordResponse response = recordService.createRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Record created successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ANALYST', 'ROLE_ADMIN')")
    @Operation(summary = "List financial records", description = "Returns paginated and filtered financial records (ANALYST, ADMIN)")
    public ResponseEntity<ApiResponse<Page<RecordResponse>>> getRecords(
            @Parameter(description = "Filter by transaction type") @RequestParam(required = false) TransactionType type,
            @Parameter(description = "Filter by category name") @RequestParam(required = false) String category,
            @Parameter(description = "Start date (yyyy-MM-dd)") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        Page<RecordResponse> records = recordService.getRecords(type, category, startDate, endDate, page, size);
        return ResponseEntity.ok(ApiResponse.success(records, "Records retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ANALYST', 'ROLE_ADMIN')")
    @Operation(summary = "Get record by ID", description = "Returns a single financial record by UUID (ANALYST, ADMIN)")
    public ResponseEntity<ApiResponse<RecordResponse>> getRecordById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(recordService.getRecordById(id), "Record retrieved successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Update a financial record", description = "Updates an existing financial record (ADMIN only)")
    public ResponseEntity<ApiResponse<RecordResponse>> updateRecord(
            @PathVariable UUID id,
            @Valid @RequestBody RecordRequest request) {
        return ResponseEntity.ok(ApiResponse.success(recordService.updateRecord(id, request), "Record updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Delete a financial record", description = "Permanently deletes a financial record (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(@PathVariable UUID id) {
        recordService.deleteRecord(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Record deleted successfully"));
    }
}
