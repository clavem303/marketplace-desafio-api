package br.edu.fiap.marketplace.dto;

import br.edu.fiap.marketplace.entity.Carrinho;
import br.edu.fiap.marketplace.entity.StatusCarrinho;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Carrinho com os dados resumidos dos relacionamentos")
public record CarrinhoResponse(
        @Schema(example = "10") Long id,
        @Schema(example = "1") Long usuarioId,
        @Schema(example = "Mariana Costa") String usuarioNome,
        @Schema(example = "3") Long produtoId,
        @Schema(example = "Teclado mecânico") String produtoNome,
        @Schema(example = "299.90") BigDecimal precoUnitario,
        @Schema(example = "2") Integer quantidade,
        @Schema(example = "599.80") BigDecimal total,
        @Schema(example = "ABERTO") StatusCarrinho status,
        @Schema(example = "2026-10-01T15:00:00Z") Instant criadoEm) {

    public static CarrinhoResponse de(Carrinho carrinho) {
        return new CarrinhoResponse(
                carrinho.getId(),
                carrinho.getUsuario().getId(),
                carrinho.getUsuario().getNome(),
                carrinho.getProduto().getId(),
                carrinho.getProduto().getNome(),
                carrinho.getProduto().getPreco(),
                carrinho.getQuantidade(),
                carrinho.calcularTotal(),
                carrinho.getStatus(),
                carrinho.getCriadoEm());
    }
}
