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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/** Confirma o resultado de pagamento de um carrinho pertencente a um usuário. */
@Entity
@Table(name = "confirmacoes_pagamento")
public class ConfirmacaoPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrinho_id", nullable = false, unique = true)
    private Carrinho carrinho;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "id_pagamento", nullable = false, unique = true, length = 80)
    private String idPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPagamento status;

    @Column(name = "valor_pago", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorPago;

    @Column(name = "confirmado_em")
    private Instant confirmadoEm;

    protected ConfirmacaoPagamento() {
    }

    public ConfirmacaoPagamento(
            Carrinho carrinho,
            Usuario usuario,
            String idPagamento,
            BigDecimal valorPago) {
        if (carrinho == null) {
            throw new RegraNegocioException("O carrinho é obrigatório.", HttpStatus.BAD_REQUEST);
        }
        if (usuario == null) {
            throw new RegraNegocioException("O usuário é obrigatório.", HttpStatus.BAD_REQUEST);
        }
        if (idPagamento == null || idPagamento.isBlank()) {
            throw new RegraNegocioException("O identificador do pagamento é obrigatório.", HttpStatus.BAD_REQUEST);
        }
        if (valorPago == null || valorPago.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O valor pago deve ser maior que zero.", HttpStatus.BAD_REQUEST);
        }
        if (carrinho.getStatus() != StatusCarrinho.ABERTO) {
            throw new RegraNegocioException("Não é possível iniciar pagamento para um carrinho que não está aberto.");
        }
        if (carrinho.getUsuario() != null
                && carrinho.getUsuario().getId() != null
                && usuario.getId() != null
                && !Objects.equals(carrinho.getUsuario().getId(), usuario.getId())) {
            throw new RegraNegocioException("O usuário informado não é o dono do carrinho.");
        } else if (carrinho.getUsuario() != null
                && (carrinho.getUsuario().getId() == null || usuario.getId() == null)
                && !carrinho.getUsuario().equals(usuario)) {
            throw new RegraNegocioException("O usuário informado não é o dono do carrinho.");
        }
        if (valorPago.compareTo(carrinho.calcularTotal()) != 0) {
            throw new RegraNegocioException(
                    "O valor pago deve ser exatamente igual ao total do carrinho.",
                    HttpStatus.BAD_REQUEST);
        }

        this.carrinho = carrinho;
        this.usuario = usuario;
        this.idPagamento = idPagamento.trim();
        this.valorPago = valorPago;
        this.status = StatusPagamento.PENDENTE;
    }

    /** TODO aprovar somente pagamento pendente e finalizar o carrinho. */
    public void aprovar() {
        exigirStatusPendente("Apenas pagamentos pendentes podem ser aprovados.");
        this.carrinho.finalizar();
        this.status = StatusPagamento.PAGO;
        this.confirmadoEm = Instant.now();
    }

    /** TODO recusar somente pagamento pendente. */
    public void recusar() {
        exigirStatusPendente("Apenas pagamentos pendentes podem ser recusados.");
        this.status = StatusPagamento.RECUSADO;
        this.confirmadoEm = Instant.now();
    }

    /** TODO devolver verdadeiro apenas para status PAGO. */
    public boolean estaPago() {
        return this.status == StatusPagamento.PAGO;
    }

    private void exigirStatusPendente(String mensagem) {
        if (this.status != StatusPagamento.PENDENTE) {
            throw new RegraNegocioException(mensagem);
        }
    }

    public Long getId() { return id; }
    public Carrinho getCarrinho() { return carrinho; }
    public Usuario getUsuario() { return usuario; }
    public String getIdPagamento() { return idPagamento; }
    public StatusPagamento getStatus() { return status; }
    public BigDecimal getValorPago() { return valorPago; }
    public Instant getConfirmadoEm() { return confirmadoEm; }
}
