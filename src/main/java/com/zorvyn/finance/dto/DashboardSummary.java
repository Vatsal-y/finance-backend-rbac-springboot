package com.zorvyn.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dashboard financial summary")
public class DashboardSummary {

    @Schema(description = "Total income across all records", example = "23000.00")
    private Double totalIncome;

    @Schema(description = "Total expense across all records", example = "4750.00")
    private Double totalExpense;

    @Schema(description = "Net balance (income - expense)", example = "18250.00")
    private Double netBalance;
}
