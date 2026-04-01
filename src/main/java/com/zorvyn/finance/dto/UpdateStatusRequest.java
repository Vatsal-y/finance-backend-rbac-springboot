package com.zorvyn.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User status update request")
public class UpdateStatusRequest {

    @NotNull(message = "Active status is required")
    @Schema(description = "Whether the user account should be active", example = "false")
    private Boolean active;
}
