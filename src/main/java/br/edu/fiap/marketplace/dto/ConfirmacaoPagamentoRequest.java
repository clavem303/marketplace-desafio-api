package br.edu.fiap.marketplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados necessários para iniciar a confirmação do pagamento")
public record ConfirmacaoPagamentoRequest(
        @Schema(example = "10")
        @NotNull(message = "Carrinho é obrigatório.")
        Long carrinhoId,

        @Schema(example = "1")
        @NotNull(message = "Usuário é obrigatório.")
        Long usuarioId,

        @Schema(example = "PAY-2026-000123")
        @NotBlank(message = "ID do pagamento é obrigatório.")
        @Size(max = 80, message = "ID do pagamento deve possuir no máximo 80 caracteres.")
        String idPagamento) {
}
