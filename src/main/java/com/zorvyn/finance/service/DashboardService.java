package com.zorvyn.finance.service;

import com.zorvyn.finance.dto.CategoryBreakdown;
import com.zorvyn.finance.dto.DashboardSummary;
import com.zorvyn.finance.dto.MonthlyTrend;
import com.zorvyn.finance.dto.RecordResponse;
import com.zorvyn.finance.model.FinancialRecord;
import com.zorvyn.finance.model.TransactionType;
import com.zorvyn.finance.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final FinancialRecordRepository recordRepository;

    public DashboardSummary getSummary() {
        Double totalIncome = recordRepository.sumByType(TransactionType.INCOME);
        Double totalExpense = recordRepository.sumByType(TransactionType.EXPENSE);

        log.info("Dashboard summary requested — income: {}, expense: {}, net: {}",
                totalIncome, totalExpense, totalIncome - totalExpense);

        return DashboardSummary.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netBalance(totalIncome - totalExpense)
                .build();
    }

    public List<CategoryBreakdown> getCategoryBreakdown() {
        List<Object[]> results = recordRepository.getCategoryBreakdown();
        log.info("Category breakdown requested — {} categories found", results.size());
        return results.stream()
                .map(row -> CategoryBreakdown.builder()
                        .category((String) row[0])
                        .type(((TransactionType) row[1]).name())
                        .totalAmount((Double) row[2])
                        .build())
                .collect(Collectors.toList());
    }

    public List<RecordResponse> getRecentTransactions() {
        List<FinancialRecord> records = recordRepository.findTop10ByOrderByCreatedAtDesc();
        log.info("Recent transactions requested — {} records returned", records.size());
        return records.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<MonthlyTrend> getMonthlyTrend() {
        List<Object[]> results = recordRepository.getMonthlyTrend();

        // Group results by year-month, aggregating income and expense
        Map<String, MonthlyTrend> trendMap = new LinkedHashMap<>();

        for (Object[] row : results) {
            int year = (Integer) row[0];
            int month = (Integer) row[1];
            TransactionType type = (TransactionType) row[2];
            Double amount = (Double) row[3];

            String key = year + "-" + month;
            MonthlyTrend trend = trendMap.computeIfAbsent(key, k ->
                    MonthlyTrend.builder()
                            .year(year)
                            .month(month)
                            .totalIncome(0.0)
                            .totalExpense(0.0)
                            .netBalance(0.0)
                            .build());

            if (type == TransactionType.INCOME) {
                trend.setTotalIncome(trend.getTotalIncome() + amount);
            } else {
                trend.setTotalExpense(trend.getTotalExpense() + amount);
            }
            trend.setNetBalance(trend.getTotalIncome() - trend.getTotalExpense());
        }

        log.info("Monthly trend requested — {} months of data", trendMap.size());
        return new ArrayList<>(trendMap.values());
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
