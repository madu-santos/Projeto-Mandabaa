package com.mandabaa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal do Mandabaa: é por aqui que o Spring Boot inicia a aplicação.
 * Todo o sistema roda em UMA aplicação só (monólito modular), dividida em pacotes por módulo.
 */
@SpringBootApplication
public class AplicacaoMandabaa {

    public static void main(String[] args) {
        SpringApplication.run(AplicacaoMandabaa.class, args);
    }
}
