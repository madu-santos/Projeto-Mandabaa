package com.mandabaa.carteira.servico;

import com.mandabaa.carteira.dto.ItemResgateDTO;
import com.mandabaa.carteira.dto.RespostaResgateDTO;
import com.mandabaa.carteira.dto.SolicitacaoResgateDTO;
import com.mandabaa.carteira.repositorio.RepositorioResgate;
import com.mandabaa.parceiros.repositorio.RepositorioOferta;
import com.mandabaa.usuarios.repositorio.RepositorioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Regras do resgate de benefícios e consulta dos benefícios resgatados (US03).
 *
 * ESTADO ATUAL = apenas o ESQUELETO da fase RED do TDD (Sprint 5.1):
 * as assinaturas existem, mas NÃO há regra de negócio ainda. É de propósito, para que os
 * testes unitários da Pessoa 4 falhem primeiro (vermelho) antes de a Pessoa 2 implementar (verde).
 */
@Service
@RequiredArgsConstructor
public class ServicoResgate {

    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioOferta repositorioOferta;
    private final RepositorioResgate repositorioResgate;

    /** Processa a troca de pontos por cupom ou cashback validando saldo e oferta. */
    public RespostaResgateDTO realizarResgate(SolicitacaoResgateDTO solicitacao) {
        // Sem implementação ainda (fase RED).
        return null;
    }

    /** Lista todos os benefícios já resgatados por um usuário com sua situação atual (US03 - CA07). */
    public List<ItemResgateDTO> listarResgatesDoUsuario(Long usuarioId) {
        // Sem implementação ainda (fase RED).
        return null;
    }
}
