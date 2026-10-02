package br.edu.fiap.marketplace.repository;

import br.edu.fiap.marketplace.entity.CatalogoProduto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** TODO adicionar consultas do catálogo conforme os casos de uso. */
public interface CatalogoProdutoRepository extends JpaRepository<CatalogoProduto, Long> {

    List<CatalogoProduto> findByAtivoTrue();
    boolean existsByNomeIgnoreCase(String nome);
}
