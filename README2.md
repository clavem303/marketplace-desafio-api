# Marketplace Desafio API — Vitrine Express

API RESTful multicamada desenvolvida com **Java 25** e **Spring Boot** para validar o MVP de comércio eletrónico da **Vitrine Express**[cite: 1]. O sistema abrange desde o registo seguro de utilizadores e autenticação via JWT até à gestão de catálogo, carrinho de compras e confirmação transacional de pagamentos[cite: 1].

---

## Tecnologias Utilizadas

* **Linguagem:** Java 25[cite: 1]
* **Framework:** Spring Boot (Web, Data JPA, Validation, Security, OAuth2 Resource Server)[cite: 1, 14]
* **Base de Dados:** PostgreSQL[cite: 1]
* **Segurança:** Hash de palavras-passe com **BCrypt** e autenticação stateless com **Bearer JWT (HS256)**[cite: 1, 14, 15, 17]
* **Documentação:** OpenAPI 3 / Swagger UI (`springdoc-openapi`)[cite: 1, 16, 36]
* **Testes:** JUnit 5, AssertJ, Mockito e Spring MockMvc[cite: 1]

---

## Decisões de Negócio e Arquitetura

1. **Momento da Reserva e Baixa de Estoque:**
    * **Decisão do Grupo:** Seguindo o escopo mínimo recomendado na especificação, **a baixa efetiva do estoque ocorre exclusivamente no momento de aprovar o pagamento** (`PATCH /api/pagamentos/{id}/aprovar`)[cite: 1].
    * **Validação Prévia:** Durante a criação do carrinho (`POST /api/carrinhos`) e na alteração de quantidade (`PATCH /api/carrinhos/{id}/quantidade`), a API verifica se há saldo disponível em estoque para impedir que o cliente monte um carrinho inviável, mas sem reter o saldo antes do pagamento[cite: 1].
2. **Transação Atómica no Pagamento (`@Transactional`):**
    * A aprovação do pagamento (`ConfirmacaoPagamentoService.aprovar`) executa dentro de uma única transação de base de dados[cite: 1]. Ela desconta o estoque do produto (`produto.baixarEstoque`), finaliza o carrinho (`carrinho.finalizar`) e marca o pagamento como `PAGO` (`pagamento.aprovar`)[cite: 1, 10]. Caso o estoque tenha sido consumido por outra compra concorrente ou qualquer etapa falhe, o *rollback* automático garante que nenhuma alteração parcial permaneça no PostgreSQL[cite: 1].
3. **Proteção do Domínio (Rich Domain Model):**
    * As entidades (`Usuario`, `CatalogoProduto`, `Carrinho`, `ConfirmacaoPagamento`) não expõem *setters* genéricos públicos[cite: 1, 8, 9, 10, 13]. Todas as mutações ocorrem por meio de métodos de negócio que validam as invariantes (ex.: proibição de preço zero/negativo, proibição de alterar carrinho fechado, transição única de pagamento de `PENDENTE` para `PAGO` ou `RECUSADO`)[cite: 1, 8, 9, 10].
4. **Segurança e Isolamento por DTOs:**
    * Nenhuma entidade JPA é exposta diretamente nos Controllers[cite: 1].
    * A senha do utilizador é recebida exclusivamente no `UsuarioRequest`/`LoginRequest` (`WRITE_ONLY`), codificada com `BCryptPasswordEncoder` no Service e jamais é devolvida no `UsuarioResponse`, logs ou mensagens de erro[cite: 1, 15, 32, 34, 35].

---

## Divisão do Grupo

| Papel | Integrante | Foco de Atuação | Integração Realizada |
| :--- | :--- | :--- | :--- |
| **1. Domínio e Banco** | *[Nome do Integrante 1]* | Entidades, invariantes de negócio e scripts SQL | Alinhamento de relacionamentos JPA/SQL com os DTOs[cite: 1] |
| **2. Usuário e Autenticação** | *[Nome do Integrante 2]* | Cadastro, BCrypt, Login, `JwtService` e `JwtAuthenticationEntryPoint` | Entrega de identidade JWT e proteção de rotas com resposta 401 padronizada[cite: 1] |
| **3. Catálogo** | *[Nome do Integrante 3]* | Produto, preço, estoque e endpoints de catálogo | Exposição de produtos ativos e controlo de saldo para o carrinho[cite: 1] |
| **4. Carrinho** | *[Nome do Integrante 4]* | Criação, alteração de quantidade, cálculo de total e cancelamento | Validação de utilizador ativo, produto ativo e saldo em estoque[cite: 1] |
| **5. Pagamento e Qualidade** | *[Nome do Integrante 5]* | Pagamento transacional, `GlobalExceptionHandler`, Swagger e Testes | Fecho atómico da compra, documentação OpenAPI e evidências[cite: 1] |

---

## Configuração da Base de Dados (PostgreSQL)

Execute os scripts localizados na pasta `sql/` exatamente na seguinte ordem[cite: 1]:

1. Conecte-se à base de dados padrão `postgres` e execute (fora de um bloco de transação)[cite: 1]:
    * `sql/01_criar_database.sql` (cria a base `marketplace_desafio`)[cite: 1]
2. Abra uma nova conexão apontando para a base `marketplace_desafio` e execute na sequência[cite: 1]:
    * `sql/02_criar_tabelas.sql` (cria as 4 tabelas, FKs, constraints `UNIQUE` e `CHECK`, e índices)[cite: 1]
    * `sql/03_carga_dados.sql` (insere os registos iniciais para demonstração)[cite: 1]
    * `sql/04_consultas_evidencias.sql` (consultas de verificação de relacionamentos e saldos)[cite: 1]

---

## Como Executar a Aplicação

### Variáveis de Ambiente Suportadas
A aplicação utiliza variáveis de ambiente com valores padrão de desenvolvimento definidos no `application.properties`[cite: 1, 36]:

| Variável | Descrição | Valor Padrão (Local) |
| :--- | :--- | :--- |
| `SERVER_PORT` | Porta HTTP da aplicação | `8082`[cite: 36] |
| `DB_URL` | URL JDBC do PostgreSQL | `jdbc:postgresql://localhost:5432/marketplace_desafio`[cite: 1, 36] |
| `DB_USER` | Utilizador do PostgreSQL | `postgres`[cite: 1, 36] |
| `DB_PASSWORD` | Palavra-passe do PostgreSQL | *(vazio — definir conforme ambiente local)*[cite: 36] |
| `BCRYPT_STRENGTH` | Custo de processamento do BCrypt | `12`[cite: 15, 36] |
| `JWT_SECRET` | Chave secreta em Base64 (mín. 32 bytes) | `c2VncmVkby1qd3QtZGlkYXRpY28tMzItYnl0ZXMtMTIzNDU2Nzg=`[cite: 17, 36] |
| `JWT_ISSUER` | Emissor validado no token JWT | `marketplace-desafio-api`[cite: 17, 36] |
| `JWT_EXPIRATION_SECONDS` | Tempo de vida do token em segundos | `900` (15 minutos)[cite: 33, 36] |

### Subindo a API via Terminal

```bash
DB_URL=jdbc:postgresql://localhost:5432/marketplace_desafio \
DB_USER=postgres \
DB_PASSWORD=sua_senha \
mvn spring-boot:run
```

### Acesso à Documentação Interativa (Swagger UI)
Com a aplicação em execução, aceda a:
* **Swagger UI:** `http://localhost:8082/swagger-ui.html`[cite: 14, 36]
* **OpenAPI JSON:** `http://localhost:8082/v3/api-docs`[cite: 14, 36]

> **Como autenticar no Swagger:** Execute o endpoint `POST /api/auth/login`, copie o valor do campo `accessToken` retornado, clique no botão **Authorize** no topo da página do Swagger e cole o token[cite: 1, 16, 33].

---

## Como Executar os Testes Automatizados

Para compilar o projeto e executar a suíte de testes unitários (`CatalogoProdutoTest`, `CarrinhoTest`) e de integração web (`CarrinhoControllerIntegrationTest`)[cite: 1, 42]:

```bash
mvn test
```

---

## Contrato HTTP e Matriz de Rotas

| Método e Rota | Acesso | Status Esperado | Descrição |
| :--- | :--- | :--- | :--- |
| `POST /api/usuarios` | Público | `201 Created` | Cadastra utilizador com BCrypt sem expor senha[cite: 1] |
| `POST /api/auth/login` | Público | `200 OK` / `401` | Autentica credenciais e emite `TokenResponse` (JWT)[cite: 1] |
| `GET /api/produtos` | Público | `200 OK` | Lista o catálogo de produtos[cite: 1] |
| `GET /api/produtos/{id}` | Público | `200 OK` / `404` | Consulta detalhes de um produto por ID[cite: 1] |
| `GET /api/usuarios/{id}` | Protegido | `200 OK` / `404` | Consulta utilizador por ID sem expor senha[cite: 1] |
| `POST /api/produtos` | Protegido | `201 Created` | Cadastra novo produto no catálogo (+ header `Location`)[cite: 1] |
| `PUT /api/produtos/{id}` | Protegido | `200 OK` | Atualiza dados cadastrais do produto[cite: 1] |
| `PATCH /api/produtos/{id}/estoque` | Protegido | `200 OK` | Repõe uma quantidade positiva no estoque[cite: 1] |
| `POST /api/carrinhos` | Protegido | `201 Created` | Cria um carrinho `ABERTO` (+ header `Location`)[cite: 1] |
| `GET /api/carrinhos/{id}` | Protegido | `200 OK` / `404` | Consulta carrinho e total calculado[cite: 1] |
| `GET /api/carrinhos/usuario/{usuarioId}` | Protegido | `200 OK` | Lista os carrinhos de um utilizador[cite: 1] |
| `PATCH /api/carrinhos/{id}/quantidade` | Protegido | `200`, `400`, `409` | Altera a quantidade de um carrinho aberto[cite: 1] |
| `DELETE /api/carrinhos/{id}` | Protegido | `204 No Content` | Cancela um carrinho aberto[cite: 1] |
| `POST /api/pagamentos` | Protegido | `201 Created` | Cria confirmação de pagamento `PENDENTE`[cite: 1] |
| `GET /api/pagamentos/{id}` | Protegido | `200 OK` / `404` | Consulta estado da confirmação de pagamento[cite: 1] |
| `PATCH /api/pagamentos/{id}/aprovar` | Protegido | `200 OK` / `409` | Baixa estoque, aprova pagamento e finaliza carrinho[cite: 1] |
| `PATCH /api/pagamentos/{id}/recusar` | Protegido | `200 OK` / `409` | Recusa pagamento pendente (transição única)[cite: 1] |

---

## Padronização de Erros (`ApiErrorResponse`)

Todas as falhas retornam um JSON padronizado pelo `GlobalExceptionHandler` e `JwtAuthenticationEntryPoint`, sem expor *stack traces* ou detalhes internos da infraestrutura[cite: 1, 2, 3, 18]:

* **`400 Bad Request`**: Campos inválidos no payload (`@Valid` / Bean Validation) ou parâmetros malformados[cite: 1, 3].
* **`401 Unauthorized`**: Credenciais inválidas no login (`CredenciaisInvalidasException`) ou token JWT ausente/inválido/expirado (`JwtAuthenticationEntryPoint`)[cite: 1, 18].
* **`404 Not Found`**: Utilizador, produto, carrinho ou pagamento inexistente (`RecursoNaoEncontradoException`)[cite: 1].
* **`409 Conflict`**: Duplicidade de e-mail, `idPagamento` ou carrinho já vinculado a pagamento (`ConflitoNegocioException`), bem como saldo de estoque insuficiente ou estado incompatível da entidade (`RegraNegocioException`)[cite: 1].

---

## Evidências e Entregáveis Extras

* As capturas de ecrã do funcionamento no Swagger, consultas no PostgreSQL e testes de falha estão disponíveis no diretório `docs/evidencias/`[cite: 1].
* A coleção de chamadas HTTP para demonstração do caminho feliz e dos cenários de erro encontra-se na raiz do projeto (`requests.http`)[cite: 1].