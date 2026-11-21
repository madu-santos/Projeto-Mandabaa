package com.mandabaa.carteira.servico;

import com.mandabaa.carteira.dto.CarteiraDTO;
import com.mandabaa.usuarios.repositorio.RepositorioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Regras de consulta de saldo, equivalente em reais e regras da carteira (US05).
 *
 * ESTADO ATUAL = apenas o ESQUELETO da fase RED do TDD (Sprint 5.1):
 * sem regra de negócio ainda, para que os testes unitários falhem primeiro.
 */
@Service
@RequiredArgsConstructor
public class ServicoCarteira {

    private final RepositorioUsuario repositorioUsuario;

    /** Consulta saldo de pontos, converte para reais e informa se o usuário está sem saldo. */
    public CarteiraDTO consultarCarteira(Long usuarioId) {
        // Sem implementação ainda (fase RED).
        return null;
    }
}
