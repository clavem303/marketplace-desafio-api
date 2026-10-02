package br.edu.fiap.marketplace.dto;

import br.edu.fiap.marketplace.entity.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Usuário devolvido sem senha ou hash")
public record UsuarioResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Mariana Costa") String nome,
        @Schema(example = "mariana@example.com") String email,
        @Schema(example = "true") boolean ativo) {

    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.isAtivo());
    }
}
