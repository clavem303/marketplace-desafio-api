package br.edu.fiap.marketplace.dto;

import br.edu.fiap.marketplace.entity.CatalogoProduto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Produto disponível no catálogo")
public record CatalogoProdutoResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Teclado mecânico") String nome,
        @Schema(example = "Teclado ABNT2 com iluminação") String descricao,
        @Schema(example = "299.90") BigDecimal preco,
        @Schema(example = "15") Integer estoque,
        @Schema(example = "true") boolean ativo) {

    public static CatalogoProdutoResponse de(CatalogoProduto produto) {
        return new CatalogoProdutoResponse(
                produto.getId(), produto.getNome(), produto.getDescricao(),
                produto.getPreco(), produto.getEstoque(), produto.isAtivo());
    }
}
