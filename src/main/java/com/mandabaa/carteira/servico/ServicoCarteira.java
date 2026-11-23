package com.mandabaa.carteira.servico;

import com.mandabaa.carteira.dto.CarteiraDTO;
import com.mandabaa.comum.excecao.RecursoNaoEncontradoExcecao;
import com.mandabaa.usuarios.entidade.Usuario;
import com.mandabaa.usuarios.repositorio.RepositorioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Serviço responsável pela consulta de saldo e carteira de pontos (US05).
 */
@Service
@RequiredArgsConstructor
public class ServicoCarteira {

    private static final BigDecimal VALOR_POR_PONTO = new BigDecimal("0.02");
    public static final String REGRA_CALCULO_OFICIAL = "Cada 1 kg de PET validado vale 50 pontos • 100 pontos = R$ 2,00";
    private static final String MENSAGEM_SEM_SALDO = 
            "Você ainda não possui pontos acumulados. Entregue garrafas PET em um ponto de coleta para começar a pontuar!";

    private final RepositorioUsuario repositorioUsuario;

    @Transactional(readOnly = true)
    public CarteiraDTO consultarCarteira(Long usuarioId) {
        validarParametroUsuarioId(usuarioId);

        Usuario usuario = buscarUsuarioOuFalhar(usuarioId);
        int saldoPontos = extrairSaldoPontos(usuario);
        BigDecimal equivalenteReais = calcularEquivalenteEmReais(saldoPontos);
        boolean semSaldo = (saldoPontos == 0);
        String mensagemSaldo = construirMensagemSaldo(semSaldo, saldoPontos);

        return CarteiraDTO.builder()
                .usuarioId(usuario.getId())
                .nomeUsuario(usuario.getNome())
                .saldoPontos(saldoPontos)
                .equivalenteReais(equivalenteReais)
                .regraCalculo(REGRA_CALCULO_OFICIAL)
                .semSaldo(semSaldo)
                .mensagemSaldo(mensagemSaldo)
                .build();
    }

    private void validarParametroUsuarioId(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("O ID do usuário não pode ser nulo.");
        }
    }

    private Usuario buscarUsuarioOuFalhar(Long usuarioId) {
        return repositorioUsuario.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoExcecao("Usuário não encontrado: " + usuarioId));
    }

    private int extrairSaldoPontos(Usuario usuario) {
        return usuario.getSaldoPontos() != null ? usuario.getSaldoPontos() : 0;
    }

    private BigDecimal calcularEquivalenteEmReais(int saldoPontos) {
        return BigDecimal.valueOf(saldoPontos)
                .multiply(VALOR_POR_PONTO)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private String construirMensagemSaldo(boolean semSaldo, int saldoPontos) {
        if (semSaldo) {
            return MENSAGEM_SEM_SALDO;
        }
        return "Você possui " + saldoPontos + " pontos disponíveis na sua carteira.";
    }
}
