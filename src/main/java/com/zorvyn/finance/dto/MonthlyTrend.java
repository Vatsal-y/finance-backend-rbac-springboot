package com.zorvyn.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyTrend {

    private int year;
    private int month;
    private Double totalIncome;
    private Double totalExpense;
    private Double netBalance;
}
