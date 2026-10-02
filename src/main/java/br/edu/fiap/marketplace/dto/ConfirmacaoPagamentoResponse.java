package br.edu.fiap.marketplace.dto;

import br.edu.fiap.marketplace.entity.ConfirmacaoPagamento;
import br.edu.fiap.marketplace.entity.StatusPagamento;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Resultado atual da confirmação de pagamento")
public record ConfirmacaoPagamentoResponse(
        @Schema(example = "20") Long id,
        @Schema(example = "10") Long carrinhoId,
        @Schema(example = "1") Long usuarioId,
        @Schema(example = "PAY-2026-000123") String idPagamento,
        @Schema(example = "PAGO") StatusPagamento status,
        @Schema(example = "599.80") BigDecimal valorPago,
        @Schema(example = "2026-10-01T15:10:00Z") Instant confirmadoEm) {

    public static ConfirmacaoPagamentoResponse de(ConfirmacaoPagamento pagamento) {
        return new ConfirmacaoPagamentoResponse(
                pagamento.getId(),
                pagamento.getCarrinho().getId(),
                pagamento.getUsuario().getId(),
                pagamento.getIdPagamento(),
                pagamento.getStatus(),
                pagamento.getValorPago(),
                pagamento.getConfirmadoEm());
    }
}
