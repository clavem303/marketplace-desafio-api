package br.edu.fiap.marketplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciais usadas para solicitar o JWT")
public record LoginRequest(
        @Schema(example = "mariana@example.com")
        @NotBlank(message = "E-mail é obrigatório.")
        @Email(message = "E-mail deve possuir formato válido.")
        String email,

        @Schema(example = "Senha@123", accessMode = Schema.AccessMode.WRITE_ONLY)
        @NotBlank(message = "Senha é obrigatória.")
        String senha) {
}
