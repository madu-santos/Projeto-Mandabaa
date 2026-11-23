package com.mandabaa.carteira.servico;

import com.mandabaa.carteira.dto.RespostaResgateDTO;
import com.mandabaa.carteira.dto.SolicitacaoResgateDTO;
import com.mandabaa.carteira.entidade.Resgate;
import com.mandabaa.carteira.repositorio.RepositorioResgate;
import com.mandabaa.comum.excecao.OfertaIndisponivelExcecao;
import com.mandabaa.comum.excecao.SaldoInsuficienteExcecao;
import com.mandabaa.parceiros.entidade.Oferta;
import com.mandabaa.parceiros.repositorio.RepositorioOferta;
import com.mandabaa.usuarios.entidade.Usuario;
import com.mandabaa.usuarios.repositorio.RepositorioUsuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes Unitários da US03 — Resgate de Benefícios e Cashback (Sprint 5.1).
 */
@ExtendWith(MockitoExtension.class)
class ServicoResgateTest {

    @Mock
    private RepositorioUsuario repositorioUsuario;

    @Mock
    private RepositorioOferta repositorioOferta;

    @Mock
    private RepositorioResgate repositorioResgate;

    @InjectMocks
    private ServicoResgate servicoResgate;

    @Test
    @DisplayName("US03 - CA01: Deve realizar resgate de cupom com sucesso quando há saldo e estoque")
    void deveRealizarResgateDeCupomComSucesso() {
        // GIVEN
        Long usuarioId = 1L;
        Long ofertaId = 20L;
        Usuario usuario = Usuario.builder().id(usuarioId).nome("Alex").saldoPontos(1000).build();
        Oferta oferta = Oferta.builder().id(ofertaId).titulo("Cupom 10%").tipo("CUPOM").custoPontos(500).estoque(5).ativa(true).build();

        SolicitacaoResgateDTO solicitacao = new SolicitacaoResgateDTO(usuarioId, ofertaId, "CUPOM", null);

        when(repositorioUsuario.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(repositorioOferta.findById(ofertaId)).thenReturn(Optional.of(oferta));
        when(repositorioResgate.save(any(Resgate.class))).thenAnswer(i -> {
            Resgate r = i.getArgument(0);
            r.setId(100L);
            return r;
        });

        // WHEN
        RespostaResgateDTO resposta = servicoResgate.realizarResgate(solicitacao);

        // THEN
        assertNotNull(resposta);
        assertEquals(500, resposta.getNovoSaldo());
        assertEquals("DISPONIVEL", resposta.getSituacao());
        assertEquals(4, oferta.getEstoque());
        assertNotNull(resposta.getCodigoComprovante());
        verify(repositorioUsuario).save(usuario);
        verify(repositorioOferta).save(oferta);
        verify(repositorioResgate).save(any(Resgate.class));
    }

    @Test
    @DisplayName("US03 - CA02: Deve lançar SaldoInsuficienteExcecao com quantidade de pontos faltantes")
    void deveLancarExcecaoQuandoSaldoInsuficienteInformandoPontosFaltantes() {
        // GIVEN
        Long usuarioId = 1L;
        Long ofertaId = 20L;
        Usuario usuario = Usuario.builder().id(usuarioId).saldoPontos(200).build();
        Oferta oferta = Oferta.builder().id(ofertaId).custoPontos(500).estoque(5).ativa(true).tipo("CUPOM").build();

        SolicitacaoResgateDTO solicitacao = new SolicitacaoResgateDTO(usuarioId, ofertaId, "CUPOM", null);

        when(repositorioUsuario.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(repositorioOferta.findById(ofertaId)).thenReturn(Optional.of(oferta));

        // WHEN / THEN
        SaldoInsuficienteExcecao ex = assertThrows(SaldoInsuficienteExcecao.class, () -> {
            servicoResgate.realizarResgate(solicitacao);
        });

        assertTrue(ex.getMessage().contains("300"));
        assertEquals(200, usuario.getSaldoPontos());
        assertEquals(5, oferta.getEstoque());
        verify(repositorioResgate, never()).save(any());
    }

    @Test
    @DisplayName("US03 - CA03: Deve lançar OfertaIndisponivelExcecao quando oferta estiver esgotada")
    void deveLancarExcecaoQuandoOfertaEsgotada() {
        // GIVEN
        Long usuarioId = 1L;
        Long ofertaId = 30L;
        Usuario usuario = Usuario.builder().id(usuarioId).saldoPontos(1000).build();
        Oferta oferta = Oferta.builder().id(ofertaId).custoPontos(500).estoque(0).ativa(true).tipo("CUPOM").build();

        SolicitacaoResgateDTO solicitacao = new SolicitacaoResgateDTO(usuarioId, ofertaId, "CUPOM", null);

        when(repositorioUsuario.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(repositorioOferta.findById(ofertaId)).thenReturn(Optional.of(oferta));

        // WHEN / THEN
        assertThrows(OfertaIndisponivelExcecao.class, () -> {
            servicoResgate.realizarResgate(solicitacao);
        });
        assertEquals(1000, usuario.getSaldoPontos());
        verify(repositorioResgate, never()).save(any());
    }

    @Test
    @DisplayName("US03 - CA04: Deve realizar resgate de cashback com chave PIX e definir situação como PAGAMENTO_PENDENTE")
    void deveRealizarResgateDeCashback() {
        // GIVEN
        Long usuarioId = 1L;
        Long ofertaId = 10L;
        Usuario usuario = Usuario.builder().id(usuarioId).saldoPontos(1000).build();
        Oferta oferta = Oferta.builder().id(ofertaId).tipo("CASHBACK").custoPontos(800).estoque(10).ativa(true).valorCashback(new BigDecimal("5.00")).build();

        SolicitacaoResgateDTO solicitacao = new SolicitacaoResgateDTO(usuarioId, ofertaId, "CASHBACK", "alex@email.com");

        when(repositorioUsuario.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(repositorioOferta.findById(ofertaId)).thenReturn(Optional.of(oferta));
        when(repositorioResgate.save(any(Resgate.class))).thenAnswer(i -> {
            Resgate r = i.getArgument(0);
            r.setId(101L);
            return r;
        });

        // WHEN
        RespostaResgateDTO resposta = servicoResgate.realizarResgate(solicitacao);

        // THEN
        assertNotNull(resposta);
        assertEquals(200, resposta.getNovoSaldo());
        assertEquals("PAGAMENTO_PENDENTE", resposta.getSituacao());
        verify(repositorioResgate).save(any(Resgate.class));
    }
}
