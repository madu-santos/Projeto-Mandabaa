package com.mandabaa.engajamento.servico;

import com.mandabaa.engajamento.dto.CampanhaDTO;
import com.mandabaa.engajamento.repositorio.RepositorioCampanha;
import com.mandabaa.usuarios.repositorio.RepositorioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Regras de campanhas temporárias e avisos (US07).
 *
 * ESTADO ATUAL = apenas o ESQUELETO da fase RED do TDD (Sprint 5.1): sem regra de negócio,
 * para que os testes unitários falhem primeiro.
 */
@Service
@RequiredArgsConstructor
public class ServicoCampanha {

    private final RepositorioCampanha repositorioCampanha;
    private final RepositorioUsuario repositorioUsuario;

    /** Lista campanhas indicando quais estão ATIVAS e quais estão EXPIRADAS. */
    public List<CampanhaDTO> listarCampanhas() {
        // Sem implementação ainda (fase RED).
        return null;
    }

    /** Lista avisos de campanhas ativas respeitando se o usuário ativou notificações (US07 - CA06). */
    public List<CampanhaDTO> listarNotificacoesDoUsuario(Long usuarioId) {
        // Sem implementação ainda (fase RED).
        return null;
    }
}
