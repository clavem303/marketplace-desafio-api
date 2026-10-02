package br.edu.fiap.marketplace.controller;

import br.edu.fiap.marketplace.dto.AtualizarQuantidadeRequest;
import br.edu.fiap.marketplace.dto.CarrinhoRequest;
import br.edu.fiap.marketplace.dto.CarrinhoResponse;
import br.edu.fiap.marketplace.service.CarrinhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/** TODO criar as rotas protegidas de criação, consulta e alteração do carrinho. */
@RestController
@RequestMapping("/api/carrinhos")
@Tag(name = "Carrinhos")
@SecurityRequirement(name = "bearerAuth")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    @PostMapping
    @Operation(summary = "Cria um novo carrinho aberto", description = "Valida usuário ativo, produto ativo e disponibilidade de estoque.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Carrinho criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuário ou produto não encontrado"),
            @ApiResponse(responseCode = "409", description = "Produto/usuário inativo ou estoque insuficiente")
    })
    public ResponseEntity<CarrinhoResponse> criar(@Valid @RequestBody CarrinhoRequest request) {
        CarrinhoResponse response = carrinhoService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta um carrinho por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrinho encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Carrinho não encontrado")
    })
    public ResponseEntity<CarrinhoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(carrinhoService.buscarPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Lista todos os carrinhos de um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de carrinhos do usuário"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<List<CarrinhoResponse>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carrinhoService.listarPorUsuario(usuarioId));
    }

    @PatchMapping("/{id}/quantidade")
    @Operation(summary = "Altera a quantidade de itens em um carrinho aberto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quantidade alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quantidade inválida"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Carrinho não encontrado"),
            @ApiResponse(responseCode = "409", description = "Carrinho não está aberto ou estoque insuficiente")
    })
    public ResponseEntity<CarrinhoResponse> alterarQuantidade(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarQuantidadeRequest request) {
        return ResponseEntity.ok(carrinhoService.alterarQuantidade(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancela um carrinho aberto")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Carrinho cancelado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Carrinho não encontrado"),
            @ApiResponse(responseCode = "409", description = "Carrinho já finalizado ou cancelado")
    })
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        carrinhoService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
