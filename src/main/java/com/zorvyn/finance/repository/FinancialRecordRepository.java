package com.zorvyn.finance.repository;

import com.zorvyn.finance.model.FinancialRecord;
import com.zorvyn.finance.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, UUID> {

    // Filtered query with optional parameters
    @Query("SELECT r FROM FinancialRecord r WHERE " +
           "(:type IS NULL OR r.type = :type) AND " +
           "(:category IS NULL OR r.category = :category) AND " +
           "(:startDate IS NULL OR r.date >= :startDate) AND " +
           "(:endDate IS NULL OR r.date <= :endDate) " +
           "ORDER BY r.date DESC")
    Page<FinancialRecord> findWithFilters(
            @Param("type") TransactionType type,
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    // Sum by type
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r WHERE r.type = :type")
    Double sumByType(@Param("type") TransactionType type);

    // Category breakdown
    @Query("SELECT r.category, r.type, SUM(r.amount) FROM FinancialRecord r " +
           "GROUP BY r.category, r.type ORDER BY SUM(r.amount) DESC")
    List<Object[]> getCategoryBreakdown();

    // Recent transactions
    List<FinancialRecord> findTop10ByOrderByCreatedAtDesc();

    // Monthly trend data
    @Query("SELECT YEAR(r.date), MONTH(r.date), r.type, SUM(r.amount) " +
           "FROM FinancialRecord r " +
           "GROUP BY YEAR(r.date), MONTH(r.date), r.type " +
           "ORDER BY YEAR(r.date) DESC, MONTH(r.date) DESC")
    List<Object[]> getMonthlyTrend();
}
