package br.edu.fiap.marketplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Produto e quantidade escolhidos por um usuário")
public record CarrinhoRequest(
        @Schema(example = "1")
        @NotNull(message = "Usuário é obrigatório.")
        Long usuarioId,

        @Schema(example = "3")
        @NotNull(message = "Produto é obrigatório.")
        Long produtoId,

        @Schema(example = "2")
        @NotNull(message = "Quantidade é obrigatória.")
        @Min(value = 1, message = "Quantidade deve ser pelo menos 1.")
        Integer quantidade) {
}
