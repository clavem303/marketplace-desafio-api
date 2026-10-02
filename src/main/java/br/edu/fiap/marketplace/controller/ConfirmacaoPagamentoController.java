package br.edu.fiap.marketplace.controller;

import br.edu.fiap.marketplace.dto.ConfirmacaoPagamentoRequest;
import br.edu.fiap.marketplace.dto.ConfirmacaoPagamentoResponse;
import br.edu.fiap.marketplace.service.ConfirmacaoPagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/** TODO criar as rotas protegidas de confirmação e consulta do pagamento. */
@RestController
@RequestMapping("/api/pagamentos")
@Tag(name = "Pagamentos")
@SecurityRequirement(name = "bearerAuth")
public class ConfirmacaoPagamentoController {

    private final ConfirmacaoPagamentoService pagamentoService;

    public ConfirmacaoPagamentoController(ConfirmacaoPagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping
    @Operation(summary = "Cria uma confirmação de pagamento pendente para um carrinho")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Confirmação de pagamento iniciada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Carrinho ou usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Pagamento duplicado, usuário diferente do dono ou carrinho fechado")
    })
    public ResponseEntity<ConfirmacaoPagamentoResponse> criar(
            @Valid @RequestBody ConfirmacaoPagamentoRequest request) {
        ConfirmacaoPagamentoResponse response = pagamentoService.iniciarConfirmacao(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta uma confirmação de pagamento por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagamento encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Pagamento não encontrado")
    })
    public ResponseEntity<ConfirmacaoPagamentoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.buscarPorId(id));
    }

    @PatchMapping("/{id}/aprovar")
    @Operation(summary = "Aprova o pagamento, baixa o estoque e finaliza o carrinho atomicamente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagamento aprovado e carrinho finalizado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Pagamento não encontrado"),
            @ApiResponse(responseCode = "409", description = "Pagamento já processado ou estoque insuficiente")
    })
    public ResponseEntity<ConfirmacaoPagamentoResponse> aprovar(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.aprovar(id));
    }

    @PatchMapping("/{id}/recusar")
    @Operation(summary = "Recusa um pagamento pendente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagamento recusado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Pagamento não encontrado"),
            @ApiResponse(responseCode = "409", description = "Pagamento já processado anteriormente")
    })
    public ResponseEntity<ConfirmacaoPagamentoResponse> recusar(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.recusar(id));
    }
}
