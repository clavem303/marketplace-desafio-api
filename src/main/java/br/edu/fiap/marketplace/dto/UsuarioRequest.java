package br.edu.fiap.marketplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados públicos usados no cadastro do usuário")
public record UsuarioRequest(
        @Schema(example = "Mariana Costa")
        @NotBlank(message = "Nome é obrigatório.")
        @Size(max = 120, message = "Nome deve possuir no máximo 120 caracteres.")
        String nome,

        @Schema(example = "mariana@example.com")
        @NotBlank(message = "E-mail é obrigatório.")
        @Email(message = "E-mail deve possuir formato válido.")
        @Size(max = 160, message = "E-mail deve possuir no máximo 160 caracteres.")
        String email,

        @Schema(example = "Senha@123", accessMode = Schema.AccessMode.WRITE_ONLY)
        @NotBlank(message = "Senha é obrigatória.")
        @Size(min = 8, max = 72, message = "Senha deve possuir entre 8 e 72 caracteres.")
        String senha) {
}
