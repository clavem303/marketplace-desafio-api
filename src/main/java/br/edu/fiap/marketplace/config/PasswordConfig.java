package br.edu.fiap.marketplace.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Configura o BCrypt usado no cadastro e na autenticação dos usuários. */
@Configuration
public class PasswordConfig {

    @Bean
    PasswordEncoder passwordEncoder(
            @Value("${security.password.bcrypt-strength:12}") int strength) {
        return new BCryptPasswordEncoder(strength);
    }
}
