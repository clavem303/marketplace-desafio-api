package br.edu.fiap.marketplace.service;

import br.edu.fiap.marketplace.dto.AtualizarQuantidadeRequest;
import br.edu.fiap.marketplace.dto.CarrinhoRequest;
import br.edu.fiap.marketplace.dto.CarrinhoResponse;
import br.edu.fiap.marketplace.entity.Carrinho;
import br.edu.fiap.marketplace.entity.CatalogoProduto;
import br.edu.fiap.marketplace.entity.Usuario;
import br.edu.fiap.marketplace.exception.RecursoNaoEncontradoException;
import br.edu.fiap.marketplace.exception.RegraNegocioException;
import br.edu.fiap.marketplace.repository.CarrinhoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** TODO coordenar usuário, produto, estoque, quantidade e estado do carrinho. */
@Service
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final UsuarioService usuarioService;
    private final CatalogoProdutoService produtoService;

    public CarrinhoService(
            CarrinhoRepository carrinhoRepository,
            UsuarioService usuarioService,
            CatalogoProdutoService produtoService) {
        this.carrinhoRepository = carrinhoRepository;
        this.usuarioService = usuarioService;
        this.produtoService = produtoService;
    }

    @Transactional
    public CarrinhoResponse criar(CarrinhoRequest request) {
        Usuario usuario = usuarioService.buscarEntidadePorId(request.usuarioId());
        CatalogoProduto produto = produtoService.buscarEntidadePorId(request.produtoId());

        if (produto.getEstoque() < request.quantidade()) {
            throw new RegraNegocioException("Estoque insuficiente para a quantidade solicitada.");
        }

        Carrinho carrinho = new Carrinho(usuario, produto, request.quantidade());
        return CarrinhoResponse.de(carrinhoRepository.save(carrinho));
    }

    @Transactional(readOnly = true)
    public CarrinhoResponse buscarPorId(Long id) {
        return CarrinhoResponse.de(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public List<CarrinhoResponse> listarPorUsuario(Long usuarioId) {
        usuarioService.buscarEntidadePorId(usuarioId);
        return carrinhoRepository.findByUsuarioId(usuarioId).stream()
                .map(CarrinhoResponse::de)
                .toList();
    }

    @Transactional
    public CarrinhoResponse alterarQuantidade(Long id, AtualizarQuantidadeRequest request) {
        Carrinho carrinho = buscarEntidadePorId(id);

        if (carrinho.getProduto().getEstoque() < request.quantidade()) {
            throw new RegraNegocioException("Estoque insuficiente para a nova quantidade solicitada.");
        }

        carrinho.alterarQuantidade(request.quantidade());
        return CarrinhoResponse.de(carrinhoRepository.save(carrinho));
    }

    @Transactional
    public void cancelar(Long id) {
        Carrinho carrinho = buscarEntidadePorId(id);
        carrinho.cancelar();
        carrinhoRepository.save(carrinho);
    }

    @Transactional(readOnly = true)
    public Carrinho buscarEntidadePorId(Long id) {
        return carrinhoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Carrinho não encontrado com ID: " + id));
    }
}
