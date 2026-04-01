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
@Schema(description = "Monthly income/expense trend data")
public class MonthlyTrend {

    @Schema(description = "Year", example = "2024")
    private int year;

    @Schema(description = "Month (1-12)", example = "1")
    private int month;

    @Schema(description = "Total income for the month", example = "8000.00")
    private Double totalIncome;

    @Schema(description = "Total expense for the month", example = "1650.00")
    private Double totalExpense;

    @Schema(description = "Net balance for the month", example = "6350.00")
    private Double netBalance;
}
