-- Execute depois de 02_criar_tabelas.sql.
-- A senha didática dos usuários é Senha@123. Somente hashes BCrypt são gravados.

INSERT INTO public.usuarios (nome, email, senha, ativo)
VALUES
    ('Mariana Costa', 'mariana@example.com',
     '$2a$12$T.dVorfXM4HXJHAH/S7rp.phjQh75m1xvVIsZonYjmRCihmUwp.ea', TRUE),
    ('Joao Martins', 'joao@example.com',
     '$2a$12$Wgh8hf/t.rdKB8BZFO8R.e1FCqkV7V1ni5hgw8gYnO1xoOxcwMWRy', TRUE),
    ('Aline Souza', 'aline@example.com',
     '$2a$12$W8uRmNYwiWT6NNGgnTk0se.KE2RdcDRFwEudZ2rWLdtuZX1GnP7ji', TRUE)
ON CONFLICT (email) DO UPDATE
SET nome = EXCLUDED.nome,
    senha = EXCLUDED.senha,
    ativo = EXCLUDED.ativo;

INSERT INTO public.catalogo_produtos (nome, descricao, preco, estoque, ativo)
VALUES
    ('Teclado mecânico', 'Teclado ABNT2 com iluminação', 299.90, 20, TRUE),
    ('Mouse sem fio', 'Mouse ergonômico com receptor USB', 149.90, 35, TRUE),
    ('Monitor 27 polegadas', 'Monitor QHD com entrada HDMI', 1899.90, 8, TRUE),
    ('SSD 1TB', 'Unidade de armazenamento NVMe', 499.90, 15, TRUE),
    ('Webcam Full HD', 'Webcam com microfone integrado', 259.90, 0, FALSE)
ON CONFLICT (nome) DO UPDATE
SET descricao = EXCLUDED.descricao,
    preco = EXCLUDED.preco,
    estoque = EXCLUDED.estoque,
    ativo = EXCLUDED.ativo;

-- Carrinho aberto de Mariana com duas unidades do teclado.
INSERT INTO public.carrinhos (usuario_id, produto_id, quantidade, status)
SELECT u.id, p.id, 2, 'ABERTO'
FROM public.usuarios u
CROSS JOIN public.catalogo_produtos p
WHERE u.email = 'mariana@example.com'
  AND p.nome = 'Teclado mecânico'
  AND NOT EXISTS (
      SELECT 1
      FROM public.carrinhos c
      WHERE c.usuario_id = u.id
        AND c.produto_id = p.id
        AND c.status = 'ABERTO'
  );

-- Carrinho finalizado de João com um SSD.
INSERT INTO public.carrinhos (usuario_id, produto_id, quantidade, status)
SELECT u.id, p.id, 1, 'FINALIZADO'
FROM public.usuarios u
CROSS JOIN public.catalogo_produtos p
WHERE u.email = 'joao@example.com'
  AND p.nome = 'SSD 1TB'
  AND NOT EXISTS (
      SELECT 1
      FROM public.carrinhos c
      WHERE c.usuario_id = u.id
        AND c.produto_id = p.id
        AND c.status = 'FINALIZADO'
  );

-- Pagamento aprovado do carrinho finalizado de João.
INSERT INTO public.confirmacoes_pagamento
    (carrinho_id, usuario_id, id_pagamento, status, valor_pago, confirmado_em)
SELECT c.id, c.usuario_id, 'PAY-SEED-0001', 'PAGO', p.preco * c.quantidade, CURRENT_TIMESTAMP
FROM public.carrinhos c
JOIN public.catalogo_produtos p ON p.id = c.produto_id
JOIN public.usuarios u ON u.id = c.usuario_id
WHERE u.email = 'joao@example.com'
  AND p.nome = 'SSD 1TB'
  AND c.status = 'FINALIZADO'
ON CONFLICT (id_pagamento) DO UPDATE
SET status = EXCLUDED.status,
    valor_pago = EXCLUDED.valor_pago,
    confirmado_em = EXCLUDED.confirmado_em;
