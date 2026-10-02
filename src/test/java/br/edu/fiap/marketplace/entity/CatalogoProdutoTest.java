package br.edu.fiap.marketplace.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.edu.fiap.marketplace.exception.RegraNegocioException;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * TODO implementar um teste unitário da regra de estoque ou preço.
 * Não iniciar o Spring e não usar repository neste arquivo.
 */
class CatalogoProdutoTest {

    @Test
    @DisplayName("Deve lançar exceção e manter o saldo inalterado ao tentar baixar quantidade superior ao estoque")
    void deveImpedirBaixaDeEstoqueAcimaDoSaldoDisponivel() {
        // Preparação (Arrange)
        CatalogoProduto produto = new CatalogoProduto(
                "Teclado Mecânico",
                "Teclado Switch Blue ABNT2",
                new BigDecimal("250.00"),
                5,
                true);

        // Ação e Verificação (Act & Assert)
        assertThatThrownBy(() -> produto.baixarEstoque(6))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Estoque insuficiente");

        // Verifica que o saldo permaneceu inalterado
        assertThat(produto.getEstoque()).isEqualTo(5);
    }

    @Test
    @DisplayName("Deve diminuir o estoque corretamente quando a quantidade for válida e suficiente")
    void deveBaixarEstoqueQuandoSaldoForSuficiente() {
        // Preparação (Arrange)
        CatalogoProduto produto = new CatalogoProduto(
                "Mouse Gamer",
                "Mouse 12000 DPI",
                new BigDecimal("120.00"),
                10,
                true);

        // Ação (Act)
        produto.baixarEstoque(3);

        // Verificação (Assert)
        assertThat(produto.getEstoque()).isEqualTo(7);
    }

    @Test
    @DisplayName("Deve rejeitar preço zero ou negativo ao tentar alterar preço")
    void deveRejeitarAlteracaoParaPrecoInvalido() {
        CatalogoProduto produto = new CatalogoProduto(
                "Monitor 24",
                "Monitor Full HD IPS",
                new BigDecimal("899.90"),
                4,
                true);

        assertThatThrownBy(() -> produto.alterarPreco(BigDecimal.ZERO))
                .isInstanceOf(RegraNegocioException.class);

        assertThat(produto.getPreco()).isEqualByComparingTo("899.90");
    }
}
