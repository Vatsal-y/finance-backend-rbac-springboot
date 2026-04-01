package com.zorvyn.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User details response")
public class UserResponse {

    @Schema(description = "User UUID")
    private UUID id;

    @Schema(description = "User display name", example = "Admin User")
    private String name;

    @Schema(description = "User email address", example = "admin@zorvyn.com")
    private String email;

    @Schema(description = "User role", example = "ROLE_ADMIN")
    private String role;

    @Schema(description = "Whether the account is active", example = "true")
    private boolean active;

    @Schema(description = "Account creation timestamp")
    private LocalDateTime createdAt;
}
