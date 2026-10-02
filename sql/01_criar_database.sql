-- Execute conectado ao database postgres e com autocommit ativado.
-- CREATE DATABASE não pode ser executado dentro de uma transação.
CREATE DATABASE marketplace_desafio
    WITH ENCODING = 'UTF8';

-- Depois, abra uma nova conexão apontando para marketplace_desafio.
