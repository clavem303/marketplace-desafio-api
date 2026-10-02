package br.edu.fiap.marketplace.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.edu.fiap.marketplace.exception.RegraNegocioException;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * TODO implementar um teste unitário de quantidade, total ou estado do carrinho.
 * Instanciar somente objetos Java reais.
 */
class CarrinhoTest {

    private Usuario usuarioAtivo;
    private CatalogoProduto produtoAtivo;

    @BeforeEach
    void setUp() {
        usuarioAtivo = new Usuario(
                "Maria Silva",
                "maria@email.com",
                "$2a$10$hashSimuladoValidoParaTesteUnitario123456789");

        produtoAtivo = new CatalogoProduto(
                "Headset Sem Fio",
                "Headset Bluetooth com microfone",
                new BigDecimal("150.50"),
                10,
                true);
    }

    @Test
    @DisplayName("Deve calcular o total do carrinho multiplicando o preço atual pela quantidade")
    void deveCalcularTotalCorretamente() {
        // Preparação (Arrange)
        Carrinho carrinho = new Carrinho(usuarioAtivo, produtoAtivo, 3);

        // Ação (Act)
        BigDecimal total = carrinho.calcularTotal();

        // Verificação (Assert): 150.50 * 3 = 451.50
        assertThat(total).isEqualByComparingTo(new BigDecimal("451.50"));
        assertThat(carrinho.getStatus()).isEqualTo(StatusCarrinho.ABERTO);
    }

    @Test
    @DisplayName("Não deve permitir alterar quantidade ou cancelar um carrinho já finalizado")
    void naoDevePermitirAlterarCarrinhoFinalizado() {
        // Preparação (Arrange)
        Carrinho carrinho = new Carrinho(usuarioAtivo, produtoAtivo, 2);
        carrinho.finalizar();

        // Ação e Verificação (Act & Assert)
        assertThatThrownBy(() -> carrinho.alterarQuantidade(5))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Apenas carrinhos abertos");

        assertThatThrownBy(carrinho::cancelar)
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Apenas carrinhos abertos");

        // Estado e quantidade originais permanecem intactos
        assertThat(carrinho.getStatus()).isEqualTo(StatusCarrinho.FINALIZADO);
        assertThat(carrinho.getQuantidade()).isEqualTo(2);
    }

    @Test
    @DisplayName("Não deve permitir criar carrinho com produto inativo")
    void naoDevePermitirCriarCarrinhoComProdutoInativo() {
        produtoAtivo.desativar();

        assertThatThrownBy(() -> new Carrinho(usuarioAtivo, produtoAtivo, 1))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Produto inativo");
    }
}
