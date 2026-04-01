package com.zorvyn.finance.dto;

import com.zorvyn.finance.model.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Financial record create/update request")
public class RecordRequest {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Schema(description = "Transaction amount (must be positive)", example = "5000.00")
    private Double amount;

    @NotNull(message = "Type is required")
    @Schema(description = "Transaction type", example = "INCOME")
    private TransactionType type;

    @NotBlank(message = "Category is required")
    @Size(max = 100, message = "Category must not exceed 100 characters")
    @Schema(description = "Transaction category", example = "Salary")
    private String category;

    @NotNull(message = "Date is required")
    @Schema(description = "Transaction date", example = "2024-01-15")
    private LocalDate date;

    @Size(max = 500, message = "Note must not exceed 500 characters")
    @Schema(description = "Optional note", example = "Monthly salary")
    private String note;
}
