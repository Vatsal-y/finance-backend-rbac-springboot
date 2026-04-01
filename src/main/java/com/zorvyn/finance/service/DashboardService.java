package com.zorvyn.finance.service;

import com.zorvyn.finance.dto.*;
import com.zorvyn.finance.model.FinancialRecord;
import com.zorvyn.finance.model.TransactionType;
import com.zorvyn.finance.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository recordRepository;

    public DashboardSummary getSummary() {
        Double totalIncome = recordRepository.sumByType(TransactionType.INCOME);
        Double totalExpense = recordRepository.sumByType(TransactionType.EXPENSE);

        return DashboardSummary.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netBalance(totalIncome - totalExpense)
                .build();
    }

    public List<CategoryBreakdown> getCategoryBreakdown() {
        List<Object[]> results = recordRepository.getCategoryBreakdown();
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
