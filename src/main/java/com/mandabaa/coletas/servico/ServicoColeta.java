package com.mandabaa.coletas.servico;

import com.mandabaa.coletas.dto.ColetaDTO;
import com.mandabaa.coletas.dto.HistoricoColetasDTO;
import com.mandabaa.coletas.repositorio.RepositorioColeta;
import com.mandabaa.usuarios.repositorio.RepositorioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Regras de registro e histórico de coletas de PET (US01).
 *
 * ESTADO ATUAL = apenas o ESQUELETO da fase RED do TDD (Sprint 5.1):
 * sem regra de negócio ainda, para que os testes unitários falhem primeiro.
 */
@Service
@RequiredArgsConstructor
public class ServicoColeta {

    private final RepositorioColeta repositorioColeta;
    private final RepositorioUsuario repositorioUsuario;

    /** Registra uma nova entrega validada de PET e atualiza o saldo de pontos do usuário. */
    public ColetaDTO registrarEntregaValidada(Long usuarioId, String pontoColeta, Double pesoKg, Integer bonus) {
        // Sem implementação ainda (fase RED).
        return null;
    }

    /** Consulta o histórico de entregas validadas e o total de PET entregue em kg. */
    public HistoricoColetasDTO consultarHistorico(Long usuarioId) {
        // Sem implementação ainda (fase RED).
        return null;
    }
}
