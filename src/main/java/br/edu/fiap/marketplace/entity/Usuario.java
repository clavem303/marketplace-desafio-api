package br.edu.fiap.marketplace.entity;

import br.edu.fiap.marketplace.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.http.HttpStatus;

import java.util.Locale;

/**
 * Representa a pessoa cadastrada no marketplace.
 *
 * <p>O mapeamento JPA e os construtores já estão prontos. Os comportamentos
 * permanecem como exercício e devem proteger o estado da entidade.</p>
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(nullable = false, length = 100)
    private String senha;

    @Column(nullable = false)
    private boolean ativo;

    protected Usuario() {
    }

    public Usuario(String nome, String email, String senhaHash) {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("O nome do usuário é obrigatório.", HttpStatus.BAD_REQUEST);
        }
        if (email == null || email.isBlank()) {
            throw new RegraNegocioException("O e-mail do usuário é obrigatório.", HttpStatus.BAD_REQUEST);
        }
        validarHashSenha(senhaHash);

        this.nome = nome.trim();
        this.email = email.trim().toLowerCase(Locale.ROOT);
        this.senha = senhaHash;
        this.ativo = true;
    }

    /** TODO implementar a troca do hash armazenado. */
    public void atualizarSenhaHash(String novoHash) {
        validarHashSenha(novoHash);
        this.senha = novoHash;
    }

    /** TODO permitir novamente o uso da conta. */
    public void ativar() {
        this.ativo = true;
    }

    /** TODO impedir login e novas compras. */
    public void desativar() {
        this.ativo = false;
    }

    private void validarHashSenha(String hash) {
        if (hash == null || hash.isBlank()) {
            throw new RegraNegocioException("A senha não pode ser nula ou vazia.", HttpStatus.BAD_REQUEST);
        }
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public boolean isAtivo() { return ativo; }
}
