package br.edu.fiap.marketplace.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/** TODO implementar resposta JSON personalizada para token ausente ou inválido. */

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String jsonErro = """
                {
                  "timestamp": "%s",
                  "status": %d,
                  "erro": "%s",
                  "mensagem": "Token de autenticação ausente, inválido ou expirado.",
                  "caminho": "%s",
                  "campos": {}
                }
                """.formatted(
                Instant.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                request.getRequestURI());

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonErro);
    }
}
