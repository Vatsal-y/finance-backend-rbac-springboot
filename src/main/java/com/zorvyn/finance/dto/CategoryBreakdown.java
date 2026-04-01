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
@Schema(description = "Category-wise financial breakdown")
public class CategoryBreakdown {

    @Schema(description = "Transaction category name", example = "Salary")
    private String category;

    @Schema(description = "Total amount for this category", example = "10000.00")
    private Double totalAmount;

    @Schema(description = "Transaction type", example = "INCOME")
    private String type;
}
