package com.zorvyn.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Financial record response")
public class RecordResponse {

    @Schema(description = "Record UUID")
    private UUID id;

    @Schema(description = "Transaction amount", example = "5000.00")
    private Double amount;

    @Schema(description = "Transaction type", example = "INCOME")
    private String type;

    @Schema(description = "Transaction category", example = "Salary")
    private String category;

    @Schema(description = "Transaction date", example = "2024-01-15")
    private LocalDate date;

    @Schema(description = "Optional note")
    private String note;

    @Schema(description = "ID of the user who created the record")
    private UUID userId;

    @Schema(description = "Name of the user who created the record")
    private String userName;

    @Schema(description = "Record creation timestamp")
    private LocalDateTime createdAt;
}
