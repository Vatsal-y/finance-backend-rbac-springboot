package com.zorvyn.finance.config;

import com.zorvyn.finance.model.FinancialRecord;
import com.zorvyn.finance.model.Role;
import com.zorvyn.finance.model.TransactionType;
import com.zorvyn.finance.model.User;
import com.zorvyn.finance.repository.FinancialRecordRepository;
import com.zorvyn.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FinancialRecordRepository recordRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping...");
            return;
        }

        log.info("Seeding database with sample data...");

        // Create users
        User admin = userRepository.save(User.builder()
                .name("Admin User")
                .email("admin@zorvyn.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ROLE_ADMIN)
                .active(true)
                .build());

        User analyst = userRepository.save(User.builder()
                .name("Analyst User")
                .email("analyst@zorvyn.com")
                .password(passwordEncoder.encode("analyst123"))
                .role(Role.ROLE_ANALYST)
                .active(true)
                .build());

        User viewer = userRepository.save(User.builder()
                .name("Viewer User")
                .email("viewer@zorvyn.com")
                .password(passwordEncoder.encode("viewer123"))
                .role(Role.ROLE_VIEWER)
                .active(true)
                .build());

        log.info("Created 3 users: admin, analyst, viewer");

        // Create financial records
        List<FinancialRecord> records = List.of(
                FinancialRecord.builder()
                        .amount(5000.00).type(TransactionType.INCOME)
                        .category("Salary").date(LocalDate.of(2024, 1, 15))
                        .note("Monthly salary").user(admin).build(),
                FinancialRecord.builder()
                        .amount(1200.00).type(TransactionType.EXPENSE)
                        .category("Rent").date(LocalDate.of(2024, 1, 1))
                        .note("Office rent").user(admin).build(),
                FinancialRecord.builder()
                        .amount(3000.00).type(TransactionType.INCOME)
                        .category("Freelance").date(LocalDate.of(2024, 1, 20))
                        .note("Client project payment").user(admin).build(),
                FinancialRecord.builder()
                        .amount(450.00).type(TransactionType.EXPENSE)
                        .category("Utilities").date(LocalDate.of(2024, 1, 10))
                        .note("Electricity and internet").user(admin).build(),
                FinancialRecord.builder()
                        .amount(800.00).type(TransactionType.EXPENSE)
                        .category("Software").date(LocalDate.of(2024, 2, 5))
                        .note("Annual license renewals").user(admin).build(),
                FinancialRecord.builder()
                        .amount(5000.00).type(TransactionType.INCOME)
                        .category("Salary").date(LocalDate.of(2024, 2, 15))
                        .note("Monthly salary").user(admin).build(),
                FinancialRecord.builder()
                        .amount(2500.00).type(TransactionType.INCOME)
                        .category("Investment").date(LocalDate.of(2024, 2, 20))
                        .note("Dividend returns").user(admin).build(),
                FinancialRecord.builder()
                        .amount(350.00).type(TransactionType.EXPENSE)
                        .category("Travel").date(LocalDate.of(2024, 2, 12))
                        .note("Business travel expenses").user(admin).build(),
                FinancialRecord.builder()
                        .amount(1200.00).type(TransactionType.EXPENSE)
                        .category("Rent").date(LocalDate.of(2024, 2, 1))
                        .note("Office rent").user(admin).build(),
                FinancialRecord.builder()
                        .amount(150.00).type(TransactionType.EXPENSE)
                        .category("Office Supplies").date(LocalDate.of(2024, 2, 8))
                        .note("Stationery and equipment").user(admin).build(),
                FinancialRecord.builder()
                        .amount(7500.00).type(TransactionType.INCOME)
                        .category("Consulting").date(LocalDate.of(2024, 3, 1))
                        .note("Consulting engagement").user(admin).build(),
                FinancialRecord.builder()
                        .amount(600.00).type(TransactionType.EXPENSE)
                        .category("Marketing").date(LocalDate.of(2024, 3, 10))
                        .note("Social media ad campaign").user(admin).build()
        );

        recordRepository.saveAll(records);
        log.info("Created {} sample financial records", records.size());

        log.info("==============================================");
        log.info("  DATABASE SEEDED SUCCESSFULLY");
        log.info("==============================================");
        log.info("  Seeded Users (password in parentheses):");
        log.info("    ADMIN   : admin@zorvyn.com   (admin123)");
        log.info("    ANALYST : analyst@zorvyn.com (analyst123)");
        log.info("    VIEWER  : viewer@zorvyn.com  (viewer123)");
        log.info("==============================================");
    }
}
