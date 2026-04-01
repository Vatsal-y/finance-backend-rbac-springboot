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
@Schema(description = "Authentication response containing JWT token and user details")
public class AuthResponse {

    @Schema(description = "JWT authentication token", example = "eyJhbGciOiJIUzM4NCJ9...")
    private String token;

    @Schema(description = "User email address", example = "admin@zorvyn.com")
    private String email;

    @Schema(description = "User display name", example = "Admin User")
    private String name;

    @Schema(description = "User role", example = "ROLE_ADMIN")
    private String role;
}
