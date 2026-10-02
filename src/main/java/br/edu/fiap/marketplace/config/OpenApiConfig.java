package br.edu.fiap.marketplace.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

/** Configuração completa do Swagger e do botão Authorize com Bearer JWT. */
@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Cole somente o accessToken retornado por POST /api/auth/login")
@OpenAPIDefinition(
        info = @Info(
                title = "Marketplace Desafio API",
                version = "1.0.0",
                description = "Projeto didático multicamada com catálogo, carrinho, pagamento, BCrypt e JWT.",
                contact = @Contact(name = "FIAP Trilha API Spring Boot"),
                license = @License(name = "Uso didático")),
        tags = {
                @Tag(name = "Usuários", description = "Cadastro e consulta segura de usuários"),
                @Tag(name = "Autenticação", description = "Login e emissão do JWT"),
                @Tag(name = "Catálogo", description = "Produtos disponíveis no marketplace"),
                @Tag(name = "Carrinhos", description = "Escolha de produto e quantidade"),
                @Tag(name = "Pagamentos", description = "Confirmação e consulta do pagamento")
        })
public class OpenApiConfig {
        public OpenApiConfig() {
        }
}
