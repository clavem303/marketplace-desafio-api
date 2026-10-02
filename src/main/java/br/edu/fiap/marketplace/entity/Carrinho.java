package br.edu.fiap.marketplace.entity;

import br.edu.fiap.marketplace.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Representa uma escolha simples de produto, quantidade e usuário.
 * Cada registro corresponde a um produto no carrinho do desafio.
 */
@Entity
@Table(name = "carrinhos")
public class Carrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private CatalogoProduto produto;

    @Column(nullable = false)
    private Integer quantidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusCarrinho status;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    protected Carrinho() {
    }

    public Carrinho(Usuario usuario, CatalogoProduto produto, Integer quantidade) {
        if (usuario == null) {
            throw new RegraNegocioException("O usuário do carrinho é obrigatório.", HttpStatus.BAD_REQUEST);
        }
        if (!usuario.isAtivo()) {
            throw new RegraNegocioException("Usuário inativo não pode criar carrinho.");
        }
        if (produto == null) {
            throw new RegraNegocioException("O produto do carrinho é obrigatório.", HttpStatus.BAD_REQUEST);
        }
        if (!produto.isAtivo()) {
            throw new RegraNegocioException("Produto inativo não pode ser adicionado ao carrinho.");
        }
        if (quantidade == null || quantidade <= 0) {
            throw new RegraNegocioException("A quantidade deve ser maior que zero.", HttpStatus.BAD_REQUEST);
        }

        this.usuario = usuario;
        this.produto = produto;
        this.quantidade = quantidade;
        this.status = StatusCarrinho.ABERTO;
        this.criadoEm = Instant.now();
    }

    /** TODO aceitar somente quantidade positiva enquanto o carrinho estiver aberto. */
    public void alterarQuantidade(int novaQuantidade) {
        exigirCarrinhoAberto("Apenas carrinhos abertos podem ter a quantidade alterada.");
        if (novaQuantidade <= 0) {
            throw new RegraNegocioException("A quantidade deve ser maior que zero.", HttpStatus.BAD_REQUEST);
        }
        this.quantidade = novaQuantidade;
    }

    /** TODO calcular preço do produto multiplicado pela quantidade. */
    public BigDecimal calcularTotal() {
        return this.produto.getPreco().multiply(BigDecimal.valueOf(this.quantidade));
    }

    /** TODO impedir finalizar carrinho cancelado ou já finalizado. */
    public void finalizar() {
        exigirCarrinhoAberto("Apenas carrinhos abertos podem ser finalizados.");
        this.status = StatusCarrinho.FINALIZADO;
    }

    /** TODO impedir alterações posteriores ao cancelamento. */
    public void cancelar() {
        exigirCarrinhoAberto("Apenas carrinhos abertos podem ser cancelados.");
        this.status = StatusCarrinho.CANCELADO;
    }

    private void exigirCarrinhoAberto(String mensagem) {
        if (this.status != StatusCarrinho.ABERTO) {
            throw new RegraNegocioException(mensagem);
        }
    }

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public CatalogoProduto getProduto() { return produto; }
    public Integer getQuantidade() { return quantidade; }
    public StatusCarrinho getStatus() { return status; }
    public Instant getCriadoEm() { return criadoEm; }
}
