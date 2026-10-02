package br.edu.fiap.marketplace.entity;

import br.edu.fiap.marketplace.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

/** Produto anunciado no catálogo do marketplace. */
@Entity
@Table(name = "catalogo_produtos")
public class CatalogoProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String nome;

    @Column(nullable = false, length = 300)
    private String descricao;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private Integer estoque;

    @Column(nullable = false)
    private boolean ativo;

    protected CatalogoProduto() {
    }

    public CatalogoProduto(
            String nome,
            String descricao,
            BigDecimal preco,
            Integer estoque,
            boolean ativo) {
        validarTexto(nome, "O nome do produto é obrigatório.");
        validarTexto(descricao, "A descrição do produto é obrigatória.");
        validarPreco(preco);

        if (estoque == null || estoque < 0) {
            throw new RegraNegocioException("O estoque inicial não pode ser negativo.", HttpStatus.BAD_REQUEST);
        }

        this.nome = nome.trim();
        this.descricao = descricao.trim();
        this.preco = preco;
        this.estoque = estoque;
        this.ativo = ativo;
    }

    /** TODO rejeitar preço nulo, zero ou negativo. */
    public void alterarPreco(BigDecimal novoPreco) {
        validarPreco(novoPreco);
        this.preco = novoPreco;
    }

    /** TODO diminuir o estoque sem permitir saldo negativo. */
    public void baixarEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new RegraNegocioException("A quantidade para baixa de estoque deve ser positiva.", HttpStatus.BAD_REQUEST);
        }
        if (this.estoque < quantidade) {
            throw new RegraNegocioException("Estoque insuficiente para o produto: " + this.nome);
        }
        this.estoque -= quantidade;
    }

    /** TODO aceitar somente reposição positiva. */
    public void reporEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new RegraNegocioException("A quantidade para reposição de estoque deve ser positiva.", HttpStatus.BAD_REQUEST);
        }
        this.estoque += quantidade;
    }

    /** TODO disponibilizar o produto para compra. */
    public void ativar() {

        this.ativo = true;
    }

    /** TODO retirar o produto das novas compras. */
    public void desativar() {
        this.ativo = false;
    }

    public void atualizarDados(String nome, String descricao, BigDecimal preco, boolean ativo) {
        validarTexto(nome, "O nome do produto é obrigatório.");
        validarTexto(descricao, "A descrição do produto é obrigatória.");
        alterarPreco(preco);
        this.nome = nome.trim();
        this.descricao = descricao.trim();
        this.ativo = ativo;
    }

    private void validarPreco(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O preço do produto deve ser maior que zero.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validarTexto(String texto, String mensagemErro) {
        if (texto == null || texto.isBlank()) {
            throw new RegraNegocioException(mensagemErro, HttpStatus.BAD_REQUEST);
        }
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public BigDecimal getPreco() { return preco; }
    public Integer getEstoque() { return estoque; }
    public boolean isAtivo() { return ativo; }
}
