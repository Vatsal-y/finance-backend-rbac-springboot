package com.zorvyn.finance.dto;

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
public class RecordResponse {

    private UUID id;
    private Double amount;
    private String type;
    private String category;
    private LocalDate date;
    private String note;
    private UUID userId;
    private String userName;
    private LocalDateTime createdAt;
}
