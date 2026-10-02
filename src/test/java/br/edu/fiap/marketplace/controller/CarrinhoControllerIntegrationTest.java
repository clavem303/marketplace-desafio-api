package br.edu.fiap.marketplace.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.edu.fiap.marketplace.dto.CarrinhoRequest;
import br.edu.fiap.marketplace.dto.CarrinhoResponse;
import br.edu.fiap.marketplace.entity.StatusCarrinho;
import br.edu.fiap.marketplace.exception.GlobalExceptionHandler;
import br.edu.fiap.marketplace.service.CarrinhoService;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * TODO implementar um @WebMvcTest com MockMvc e o CarrinhoService mockado.
 * O cenário mínimo deve comprovar status, JSON e validação HTTP.
 */
class CarrinhoControllerIntegrationTest {

    private MockMvc mockMvc;
    private CarrinhoService carrinhoService;

    @BeforeEach
    void setUp() {
        carrinhoService = mock(CarrinhoService.class);
        CarrinhoController controller = new CarrinhoController(carrinhoService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Deve criar um carrinho com JSON válido e retornar 201 Created, corpo JSON e header Location")
    void deveCriarCarrinhoComDadosValidos() throws Exception {
        // Preparação (Arrange)
        CarrinhoResponse responseSimulado = new CarrinhoResponse(
                10L,
                1L,
                "Mariana Costa",
                3L,
                "Teclado mecânico",
                new BigDecimal("299.90"),
                2,
                new BigDecimal("599.80"),
                StatusCarrinho.ABERTO,
                Instant.parse("2026-10-01T15:00:00Z"));

        when(carrinhoService.criar(any(CarrinhoRequest.class))).thenReturn(responseSimulado);

        String jsonValido = """
                {
                  "usuarioId": 1,
                  "produtoId": 3,
                  "quantidade": 2
                }
                """;

        // Ação e Verificação (Act & Assert)
        mockMvc.perform(post("/api/carrinhos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/carrinhos/10"))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.produtoId").value(3))
                .andExpect(jsonPath("$.quantidade").value(2))
                .andExpect(jsonPath("$.total").value(599.80))
                .andExpect(jsonPath("$.status").value("ABERTO"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o JSON de entrada possuir dados inválidos")
    void deveRetornar400QuandoRequisicaoForInvalida() throws Exception {
        // Preparação: quantidade 0 e usuarioId nulo violam o @Valid do CarrinhoRequest
        String jsonInvalido = """
                {
                  "usuarioId": null,
                  "produtoId": 3,
                  "quantidade": 0
                }
                """;

        // Ação e Verificação (Act & Assert)
        mockMvc.perform(post("/api/carrinhos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.campos.usuarioId").exists())
                .andExpect(jsonPath("$.campos.quantidade").exists());

        verifyNoInteractions(carrinhoService);
    }
}
