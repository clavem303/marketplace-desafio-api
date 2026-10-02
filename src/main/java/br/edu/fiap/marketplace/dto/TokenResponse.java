package br.edu.fiap.marketplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "JWT devolvido após um login válido")
public record TokenResponse(
        @Schema(example = "eyJhbGciOiJIUzI1NiJ9...") String accessToken,
        @Schema(example = "Bearer") String tokenType,
        @Schema(example = "900") long expiresIn,
        @Schema(example = "2026-10-01T15:15:00Z") Instant expiresAt) {
}
