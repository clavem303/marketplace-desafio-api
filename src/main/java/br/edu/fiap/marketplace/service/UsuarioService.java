package br.edu.fiap.marketplace.service;

import br.edu.fiap.marketplace.dto.UsuarioRequest;
import br.edu.fiap.marketplace.dto.UsuarioResponse;
import br.edu.fiap.marketplace.entity.Usuario;
import br.edu.fiap.marketplace.exception.ConflitoNegocioException;
import br.edu.fiap.marketplace.exception.RecursoNaoEncontradoException;
import br.edu.fiap.marketplace.repository.UsuarioRepository;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

/** TODO implementar cadastro, consulta e proteção da senha com PasswordEncoder. */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse cadastrar(UsuarioRequest request) {
        String emailNormalizado = request.email().trim().toLowerCase(Locale.ROOT);
        if (usuarioRepository.existsByEmailIgnoreCase(emailNormalizado)) {
            throw new ConflitoNegocioException("Já existe um usuário cadastrado com este e-mail.");
        }

        String senhaHash = passwordEncoder.encode(request.senha());
        Usuario usuario = new Usuario(request.nome(), emailNormalizado, senhaHash);
        Usuario salvo = usuarioRepository.save(usuario);

        return UsuarioResponse.de(salvo);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return UsuarioResponse.de(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public Usuario buscarEntidadePorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id));
    }
}
