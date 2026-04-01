package com.zorvyn.finance.controller;

import com.zorvyn.finance.dto.ApiResponse;
import com.zorvyn.finance.dto.CategoryBreakdown;
import com.zorvyn.finance.dto.DashboardSummary;
import com.zorvyn.finance.dto.MonthlyTrend;
import com.zorvyn.finance.dto.RecordResponse;
import com.zorvyn.finance.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard analytics and insights")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyAuthority('ROLE_ANALYST', 'ROLE_ADMIN')")
    @Operation(summary = "Get financial summary", description = "Returns total income, total expense, and net balance (ANALYST, ADMIN)")
    public ResponseEntity<ApiResponse<DashboardSummary>> getSummary() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getSummary(), "Summary retrieved successfully"));
    }

    @GetMapping("/category-breakdown")
    @PreAuthorize("hasAnyAuthority('ROLE_ANALYST', 'ROLE_ADMIN')")
    @Operation(summary = "Get category breakdown", description = "Returns totals grouped by category and type (ANALYST, ADMIN)")
    public ResponseEntity<ApiResponse<List<CategoryBreakdown>>> getCategoryBreakdown() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getCategoryBreakdown(), "Category breakdown retrieved successfully"));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent transactions", description = "Returns the last 10 transactions (all authenticated users)")
    public ResponseEntity<ApiResponse<List<RecordResponse>>> getRecentTransactions() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getRecentTransactions(), "Recent transactions retrieved successfully"));
    }

    @GetMapping("/monthly-trend")
    @PreAuthorize("hasAnyAuthority('ROLE_ANALYST', 'ROLE_ADMIN')")
    @Operation(summary = "Get monthly trends", description = "Returns monthly income/expense trends with net balance (ANALYST, ADMIN)")
    public ResponseEntity<ApiResponse<List<MonthlyTrend>>> getMonthlyTrend() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getMonthlyTrend(), "Monthly trends retrieved successfully"));
    }
}
