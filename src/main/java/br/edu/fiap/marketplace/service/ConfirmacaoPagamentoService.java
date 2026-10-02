package br.edu.fiap.marketplace.service;

import br.edu.fiap.marketplace.dto.ConfirmacaoPagamentoRequest;
import br.edu.fiap.marketplace.dto.ConfirmacaoPagamentoResponse;
import br.edu.fiap.marketplace.entity.Carrinho;
import br.edu.fiap.marketplace.entity.CatalogoProduto;
import br.edu.fiap.marketplace.entity.ConfirmacaoPagamento;
import br.edu.fiap.marketplace.entity.Usuario;
import br.edu.fiap.marketplace.exception.ConflitoNegocioException;
import br.edu.fiap.marketplace.exception.RecursoNaoEncontradoException;
import br.edu.fiap.marketplace.repository.ConfirmacaoPagamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** TODO implementar criação, aprovação, recusa e consulta do pagamento. */
@Service
public class ConfirmacaoPagamentoService {

    private final ConfirmacaoPagamentoRepository pagamentoRepository;
    private final CarrinhoService carrinhoService;
    private final UsuarioService usuarioService;

    public ConfirmacaoPagamentoService(
            ConfirmacaoPagamentoRepository pagamentoRepository,
            CarrinhoService carrinhoService,
            UsuarioService usuarioService) {
        this.pagamentoRepository = pagamentoRepository;
        this.carrinhoService = carrinhoService;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public ConfirmacaoPagamentoResponse iniciarConfirmacao(ConfirmacaoPagamentoRequest request) {
        if (pagamentoRepository.existsByCarrinhoId(request.carrinhoId())) {
            throw new ConflitoNegocioException("Já existe uma confirmação de pagamento para este carrinho.");
        }
        if (pagamentoRepository.existsByIdPagamento(request.idPagamento().trim())) {
            throw new ConflitoNegocioException("Já existe um pagamento registrado com este identificador externo.");
        }

        Carrinho carrinho = carrinhoService.buscarEntidadePorId(request.carrinhoId());
        Usuario usuario = usuarioService.buscarEntidadePorId(request.usuarioId());

        ConfirmacaoPagamento pagamento = new ConfirmacaoPagamento(
                carrinho,
                usuario,
                request.idPagamento(),
                carrinho.calcularTotal());

        return ConfirmacaoPagamentoResponse.de(pagamentoRepository.save(pagamento));
    }

    @Transactional(readOnly = true)
    public ConfirmacaoPagamentoResponse buscarPorId(Long id) {
        return ConfirmacaoPagamentoResponse.de(buscarEntidadePorId(id));
    }

    @Transactional
    public ConfirmacaoPagamentoResponse aprovar(Long id) {
        ConfirmacaoPagamento pagamento = buscarEntidadePorId(id);
        Carrinho carrinho = pagamento.getCarrinho();
        CatalogoProduto produto = carrinho.getProduto();

        // Operação atômica: baixa estoque, finaliza carrinho e marca pagamento como PAGO
        produto.baixarEstoque(carrinho.getQuantidade());
        pagamento.aprovar();

        return ConfirmacaoPagamentoResponse.de(pagamentoRepository.save(pagamento));
    }

    @Transactional
    public ConfirmacaoPagamentoResponse recusar(Long id) {
        ConfirmacaoPagamento pagamento = buscarEntidadePorId(id);
        pagamento.recusar();
        return ConfirmacaoPagamentoResponse.de(pagamentoRepository.save(pagamento));
    }

    @Transactional(readOnly = true)
    public ConfirmacaoPagamento buscarEntidadePorId(Long id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pagamento não encontrado com ID: " + id));
    }
}
