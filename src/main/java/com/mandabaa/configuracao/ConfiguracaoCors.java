package com.mandabaa.configuracao;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Libera o CORS para a API.
 * Serve para o navegador aceitar chamadas ao backend quando o frontend for aberto
 * fora do Spring (ex.: Live Server do VS Code). Se o frontend for servido pelo próprio
 * Spring Boot (pasta static), essa configuração nem é necessária.
 */
@Configuration
public class ConfiguracaoCors implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registro) {
        registro.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
