package br.edu.fiap.marketplace.service;

import br.edu.fiap.marketplace.dto.CatalogoProdutoRequest;
import br.edu.fiap.marketplace.dto.CatalogoProdutoResponse;
import br.edu.fiap.marketplace.entity.CatalogoProduto;
import br.edu.fiap.marketplace.exception.ConflitoNegocioException;
import br.edu.fiap.marketplace.exception.RecursoNaoEncontradoException;
import br.edu.fiap.marketplace.repository.CatalogoProdutoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** TODO implementar cadastro, listagem, busca e alterações do catálogo. */
@Service
public class CatalogoProdutoService {

    private final CatalogoProdutoRepository produtoRepository;

    public CatalogoProdutoService(CatalogoProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public List<CatalogoProdutoResponse> listarTodos() {
        return produtoRepository.findAll().stream()
                .map(CatalogoProdutoResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public CatalogoProdutoResponse buscarPorId(Long id) {
        return CatalogoProdutoResponse.de(buscarEntidadePorId(id));
    }

    @Transactional
    public CatalogoProdutoResponse cadastrar(CatalogoProdutoRequest request) {
        if (produtoRepository.existsByNomeIgnoreCase(request.nome().trim())) {
            throw new ConflitoNegocioException("Já existe um produto cadastrado com este nome.");
        }

        CatalogoProduto produto = new CatalogoProduto(
                request.nome(),
                request.descricao(),
                request.preco(),
                request.estoque(),
                request.ativo());

        return CatalogoProdutoResponse.de(produtoRepository.save(produto));
    }

    @Transactional
    public CatalogoProdutoResponse atualizar(Long id, CatalogoProdutoRequest request) {
        CatalogoProduto produto = buscarEntidadePorId(id);

        if (!produto.getNome().equalsIgnoreCase(request.nome().trim())
                && produtoRepository.existsByNomeIgnoreCase(request.nome().trim())) {
            throw new ConflitoNegocioException("Já existe outro produto cadastrado com este nome.");
        }

        produto.atualizarDados(
                request.nome(),
                request.descricao(),
                request.preco(),
                request.ativo());

        // Ajusta o estoque caso o PUT envie um valor diferente do atual
        int diferencaEstoque = request.estoque() - produto.getEstoque();
        if (diferencaEstoque > 0) {
            produto.reporEstoque(diferencaEstoque);
        } else if (diferencaEstoque < 0) {
            produto.baixarEstoque(Math.abs(diferencaEstoque));
        }

        return CatalogoProdutoResponse.de(produtoRepository.save(produto));
    }

    @Transactional
    public CatalogoProdutoResponse reporEstoque(Long id, int quantidade) {
        CatalogoProduto produto = buscarEntidadePorId(id);
        produto.reporEstoque(quantidade);
        return CatalogoProdutoResponse.de(produtoRepository.save(produto));
    }

    @Transactional(readOnly = true)
    public CatalogoProduto buscarEntidadePorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado com ID: " + id));
    }
}
