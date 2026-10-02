package br.edu.fiap.marketplace.controller;

import br.edu.fiap.marketplace.dto.LoginRequest;
import br.edu.fiap.marketplace.dto.TokenResponse;
import br.edu.fiap.marketplace.service.AutenticacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** TODO criar POST /api/auth/login usando LoginRequest e TokenResponse. */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação")
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;

    public AutenticacaoController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica o usuário e emite um token JWT", description = "Valida e-mail e senha com BCrypt e retorna o Bearer token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Formato de entrada inválido"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas ou usuário inativo")
    })
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(autenticacaoService.autenticar(request));
    }
}
