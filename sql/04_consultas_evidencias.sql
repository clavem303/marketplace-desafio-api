-- Quantidade de registros em cada tabela.
SELECT 'usuarios' AS tabela, COUNT(*) AS quantidade FROM public.usuarios
UNION ALL
SELECT 'catalogo_produtos', COUNT(*) FROM public.catalogo_produtos
UNION ALL
SELECT 'carrinhos', COUNT(*) FROM public.carrinhos
UNION ALL
SELECT 'confirmacoes_pagamento', COUNT(*) FROM public.confirmacoes_pagamento;

-- Carrinhos com usuário, produto e total calculado.
SELECT
    c.id AS carrinho_id,
    u.id AS usuario_id,
    u.nome AS usuario,
    p.id AS produto_id,
    p.nome AS produto,
    p.preco,
    c.quantidade,
    p.preco * c.quantidade AS total,
    c.status
FROM public.carrinhos c
JOIN public.usuarios u ON u.id = c.usuario_id
JOIN public.catalogo_produtos p ON p.id = c.produto_id
ORDER BY c.id;

-- Pagamentos ligados ao carrinho e ao usuário.
SELECT
    cp.id AS confirmacao_id,
    cp.id_pagamento,
    cp.status AS pagamento_status,
    cp.valor_pago,
    cp.confirmado_em,
    c.id AS carrinho_id,
    c.status AS carrinho_status,
    u.nome AS usuario,
    p.nome AS produto
FROM public.confirmacoes_pagamento cp
JOIN public.carrinhos c ON c.id = cp.carrinho_id
JOIN public.usuarios u ON u.id = cp.usuario_id
JOIN public.catalogo_produtos p ON p.id = c.produto_id
ORDER BY cp.id;

-- Produtos que não podem ser comprados por falta de estoque ou inatividade.
SELECT id, nome, estoque, ativo
FROM public.catalogo_produtos
WHERE estoque = 0 OR ativo = FALSE
ORDER BY nome;
