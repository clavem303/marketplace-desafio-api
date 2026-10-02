package br.edu.fiap.marketplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Nova quantidade de um carrinho aberto")
public record AtualizarQuantidadeRequest(
        @Schema(example = "3")
        @NotNull(message = "Quantidade é obrigatória.")
        @Min(value = 1, message = "Quantidade deve ser pelo menos 1.")
        Integer quantidade) {
}
