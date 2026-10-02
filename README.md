# Marketplace Desafio API

Projeto inicial para um grupo completar uma API multicamada com Spring Boot, JPA, PostgreSQL, BCrypt, JWT e Swagger.

## O que já está pronto

- JDK 25 e Maven;
- dependências web, JPA, validação, PostgreSQL, OAuth2 Resource Server e Springdoc;
- entidades e relacionamentos JPA;
- construtores e assinaturas dos comportamentos das entidades;
- DTOs completos com validação e `@Schema`;
- configurações de BCrypt, JWT, segurança e OpenAPI;
- `application.properties` preparado para conexão local ou por variáveis;
- scripts SQL para database, tabelas, carga e consultas;
- classes das camadas controller, service, repository, security e test.

As regras das entidades lançam `UnsupportedOperationException` e as demais camadas estão vazias de propósito. Elas fazem parte do desafio.

## Banco local

1. Execute `sql/01_criar_database.sql` conectado ao database `postgres`, com autocommit.
2. Crie uma conexão para `marketplace_desafio`.
3. Execute `sql/02_criar_tabelas.sql`.
4. Execute `sql/03_carga_dados.sql`.
5. Confira os relacionamentos com `sql/04_consultas_evidencias.sql`.

Por padrão, a aplicação usa:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/marketplace_desafio
spring.datasource.username=postgres
spring.datasource.password=
```

Também é possível configurar sem editar o arquivo:

```bash
export DB_URL='jdbc:postgresql://localhost:5432/marketplace_desafio'
export DB_USER='postgres'
export DB_PASSWORD='sua_senha'
```

## Executar

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home \
PATH=/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home/bin:$PATH \
mvn spring-boot:run
```

Swagger: `http://localhost:8082/swagger-ui.html`

OpenAPI JSON: `http://localhost:8082/v3/api-docs`

## Segurança preparada

Rotas públicas:

- `POST /api/usuarios`;
- `POST /api/auth/login`;
- `GET /api/produtos` e `GET /api/produtos/{id}`;
- Swagger e OpenAPI.

Todas as outras rotas exigem `Authorization: Bearer <token>`.

## Ordem sugerida de implementação

1. Comportamentos das entidades.
2. Exceções de domínio e novos métodos no handler global.
3. Consultas derivadas nos repositories.
4. Services e limites `@Transactional`.
5. Controllers e anotações OpenAPI das rotas.
6. `JwtService`, entrada de autenticação e login.
7. Dois testes unitários e um teste de integração.
8. Execução dos requests e conferência das tabelas.

O documento de desafio na pasta superior descreve propriedades, regras, rotas, divisão do grupo e definição de pronto.
