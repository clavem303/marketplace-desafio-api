package br.edu.fiap.marketplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Schema(description = "Dados usados para cadastrar ou atualizar um produto")
public record CatalogoProdutoRequest(
        @Schema(example = "Teclado mecânico")
        @NotBlank(message = "Nome é obrigatório.")
        @Size(max = 120, message = "Nome deve possuir no máximo 120 caracteres.")
        String nome,

        @Schema(example = "Teclado ABNT2 com iluminação")
        @NotBlank(message = "Descrição é obrigatória.")
        @Size(max = 300, message = "Descrição deve possuir no máximo 300 caracteres.")
        String descricao,

        @Schema(example = "299.90")
        @NotNull(message = "Preço é obrigatório.")
        @DecimalMin(value = "0.01", message = "Preço deve ser pelo menos 0,01.")
        BigDecimal preco,

        @Schema(example = "15")
        @NotNull(message = "Estoque é obrigatório.")
        @Min(value = 0, message = "Estoque não pode ser negativo.")
        Integer estoque,

        @Schema(example = "true")
        @NotNull(message = "Ativo é obrigatório.")
        Boolean ativo) {
}
