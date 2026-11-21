package com.mandabaa.carteira.servico;

import com.mandabaa.carteira.dto.RespostaResgateDTO;
import com.mandabaa.carteira.dto.SolicitacaoResgateDTO;
import com.mandabaa.carteira.repositorio.RepositorioResgate;
import com.mandabaa.parceiros.repositorio.RepositorioOferta;
import com.mandabaa.usuarios.repositorio.RepositorioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Regras do resgate de benefícios (US03).
 *
 * ESTADO ATUAL = apenas o ESQUELETO da fase RED do TDD (Sprint 5.1):
 * a assinatura existe, mas NÃO há regra de negócio. É de propósito: os testes
 * unitários precisam falhar (ficar vermelhos) antes de implementarmos qualquer coisa.
 *
 * As dependências abaixo são os "Mocks" que o teste vai simular com Mockito.
 * A implementação mínima (fase GREEN) será feita depois, com os testes já criados.
 */
@Service
@RequiredArgsConstructor
public class ServicoResgate {

    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioOferta repositorioOferta;
    private final RepositorioResgate repositorioResgate;

    public RespostaResgateDTO realizarResgate(SolicitacaoResgateDTO solicitacao) {
        // Sem implementação ainda (fase RED).
        return null;
    }
}
