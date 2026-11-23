package com.mandabaa.coletas.servico;

import com.mandabaa.coletas.dto.ColetaDTO;
import com.mandabaa.coletas.dto.HistoricoColetasDTO;
import com.mandabaa.coletas.entidade.Coleta;
import com.mandabaa.coletas.repositorio.RepositorioColeta;
import com.mandabaa.comum.excecao.RecursoNaoEncontradoExcecao;
import com.mandabaa.usuarios.entidade.Usuario;
import com.mandabaa.usuarios.repositorio.RepositorioUsuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes COMPLEMENTARES da US01 (Pessoa 4 — QA, Sprint 5.1).
 *
 * Fecham lacunas dos 3 testes originais de {@link ServicoColetaTest}:
 *  - CA03 não provava que o SALDO ficou igual (apenas que nada foi salvo);
 *  - não havia teste de bônus, usuário inexistente, histórico vazio e dados gravados na coleta.
 */
@ExtendWith(MockitoExtension.class)
class ServicoColetaComplementarTest {

    @Mock
    private RepositorioColeta repositorioColeta;

    @Mock
    private RepositorioUsuario repositorioUsuario;

    @InjectMocks
    private ServicoColeta servicoColeta;

    private Usuario usuarioComSaldo(int saldo) {
        return Usuario.builder().id(1L).nome("Alex Silva").saldoPontos(saldo).build();
    }

    private void prepararSalvarColeta() {
        when(repositorioColeta.save(any(Coleta.class))).thenAnswer(i -> {
            Coleta c = i.getArgument(0);
            c.setId(10L);
            return c;
        });
    }

    // ---------------------------------------------------------------- CA01 — histórico

    @Test
    @DisplayName("US01 - CA01: Histórico sem entregas deve devolver lista vazia e total de 0 kg")
    void deveDevolverHistoricoVazioParaUsuarioSemEntregas() {
        // GIVEN
        when(repositorioUsuario.existsById(1L)).thenReturn(true);
        when(repositorioColeta.findByUsuarioIdAndValidadaTrueOrderByDataEntregaDesc(1L)).thenReturn(List.of());

        // WHEN
        HistoricoColetasDTO resultado = servicoColeta.consultarHistorico(1L);

        // THEN
        assertEquals(0.0, resultado.getTotalPetKg(), 0.0001);
        assertEquals(0, resultado.getTotalPontosRecebidos());
        assertEquals(0, resultado.getTotalBonusRecebidos());
        assertTrue(resultado.getColetas().isEmpty());
    }

    @Test
    @DisplayName("US01 - CA01: Histórico deve manter a ordem recebida do repositório e trazer a data de cada entrega")
    void deveManterOrdemEDatasDasEntregasNoHistorico() {
        // GIVEN
        Usuario usuario = usuarioComSaldo(1250);
        Coleta recente = Coleta.builder().id(1L).usuario(usuario).pontoColeta("Cooperativa Verde")
                .dataEntrega(LocalDate.of(2026, 11, 14)).pesoKg(2.5).pontosRecebidos(125).bonusRecebido(25).validada(true).build();
        Coleta antiga = Coleta.builder().id(2L).usuario(usuario).pontoColeta("Ecoponto Central")
                .dataEntrega(LocalDate.of(2026, 11, 9)).pesoKg(1.8).pontosRecebidos(90).bonusRecebido(0).validada(true).build();
        when(repositorioUsuario.existsById(1L)).thenReturn(true);
        when(repositorioColeta.findByUsuarioIdAndValidadaTrueOrderByDataEntregaDesc(1L)).thenReturn(List.of(recente, antiga));

        // WHEN
        List<ColetaDTO> itens = servicoColeta.consultarHistorico(1L).getColetas();

        // THEN
        assertEquals(LocalDate.of(2026, 11, 14), itens.get(0).getDataEntrega());
        assertEquals(LocalDate.of(2026, 11, 9), itens.get(1).getDataEntrega());
        assertEquals("Ecoponto Central", itens.get(1).getPontoColeta());
        assertEquals(1.8, itens.get(1).getPesoKg(), 0.0001);
        assertEquals(90, itens.get(1).getPontosRecebidos());
        assertEquals(0, itens.get(1).getBonusRecebido());
    }

    @Test
    @DisplayName("US01: Histórico de usuário inexistente deve lançar RecursoNaoEncontradoExcecao")
    void deveLancarExcecaoAoConsultarHistoricoDeUsuarioInexistente() {
        // GIVEN
        when(repositorioUsuario.existsById(999L)).thenReturn(false);

        // WHEN / THEN
        assertThrows(RecursoNaoEncontradoExcecao.class, () -> servicoColeta.consultarHistorico(999L));
        verify(repositorioColeta, never()).findByUsuarioIdAndValidadaTrueOrderByDataEntregaDesc(any());
    }

    // ---------------------------------------------------------------- CA02 — nova entrega

    @Test
    @DisplayName("US01 - CA02: A coleta gravada deve estar validada, ligada ao usuário e com a data de hoje")
    void deveGravarColetaValidadaComDadosCorretos() {
        // GIVEN
        Usuario usuario = usuarioComSaldo(1000);
        when(repositorioUsuario.findById(1L)).thenReturn(Optional.of(usuario));
        prepararSalvarColeta();

        // WHEN
        servicoColeta.registrarEntregaValidada(1L, "Ponto Central", 2.0, 0);

        // THEN
        ArgumentCaptor<Coleta> captor = ArgumentCaptor.forClass(Coleta.class);
        verify(repositorioColeta).save(captor.capture());
        Coleta gravada = captor.getValue();
        assertSame(usuario, gravada.getUsuario());
        assertTrue(gravada.getValidada());
        assertEquals(LocalDate.now(), gravada.getDataEntrega());
        assertEquals(2.0, gravada.getPesoKg(), 0.0001);
        assertEquals(100, gravada.getPontosRecebidos());
        assertEquals(0, gravada.getBonusRecebido());
    }

    @Test
    @DisplayName("US01 - CA02: Entrega com bônus deve somar pontos base + bônus ao saldo (2 kg + 25 = 125)")
    void deveSomarBonusAoSaldoDoUsuario() {
        // GIVEN
        Usuario usuario = usuarioComSaldo(1000);
        when(repositorioUsuario.findById(1L)).thenReturn(Optional.of(usuario));
        prepararSalvarColeta();

        // WHEN
        ColetaDTO resultado = servicoColeta.registrarEntregaValidada(1L, "Ponto Central", 2.0, 25);

        // THEN
        assertEquals(100, resultado.getPontosRecebidos());
        assertEquals(25, resultado.getBonusRecebido());
        assertEquals(1125, usuario.getSaldoPontos());
    }

    @Test
    @DisplayName("US01 - CA02: Bônus nulo ou negativo deve ser tratado como zero (não pode reduzir o saldo)")
    void deveTratarBonusNuloOuNegativoComoZero() {
        // GIVEN
        Usuario usuario = usuarioComSaldo(1000);
        when(repositorioUsuario.findById(1L)).thenReturn(Optional.of(usuario));
        prepararSalvarColeta();

        // WHEN
        ColetaDTO comNulo = servicoColeta.registrarEntregaValidada(1L, "Ponto Central", 2.0, null);
        ColetaDTO comNegativo = servicoColeta.registrarEntregaValidada(1L, "Ponto Central", 2.0, -50);

        // THEN
        assertEquals(0, comNulo.getBonusRecebido());
        assertEquals(0, comNegativo.getBonusRecebido());
        assertEquals(1200, usuario.getSaldoPontos());
    }

    @Test
    @DisplayName("US01 - CA02: Pontos seguem a regra de 50 pontos por kg (1,8 kg = 90 pontos)")
    void deveCalcularCinquentaPontosPorKg() {
        // GIVEN
        Usuario usuario = usuarioComSaldo(0);
        when(repositorioUsuario.findById(1L)).thenReturn(Optional.of(usuario));
        prepararSalvarColeta();

        // WHEN
        ColetaDTO resultado = servicoColeta.registrarEntregaValidada(1L, "Ecoponto Central", 1.8, 0);

        // THEN
        assertEquals(90, resultado.getPontosRecebidos());
        assertEquals(90, usuario.getSaldoPontos());
    }

    @Test
    @DisplayName("US01 - CA02: Ponto de coleta em branco deve assumir 'Ponto Central'")
    void deveAssumirPontoCentralQuandoPontoEmBranco() {
        // GIVEN
        Usuario usuario = usuarioComSaldo(1000);
        when(repositorioUsuario.findById(1L)).thenReturn(Optional.of(usuario));
        prepararSalvarColeta();

        // WHEN
        ColetaDTO resultado = servicoColeta.registrarEntregaValidada(1L, "   ", 2.0, 0);

        // THEN
        assertEquals("Ponto Central", resultado.getPontoColeta());
    }

    @Test
    @DisplayName("US01: Registrar entrega para usuário inexistente deve lançar RecursoNaoEncontradoExcecao e não gravar")
    void deveLancarExcecaoAoRegistrarEntregaParaUsuarioInexistente() {
        // GIVEN
        when(repositorioUsuario.findById(999L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThrows(RecursoNaoEncontradoExcecao.class,
                () -> servicoColeta.registrarEntregaValidada(999L, "Ponto Central", 2.0, 0));
        verify(repositorioColeta, never()).save(any());
        verify(repositorioUsuario, never()).save(any());
    }

    // ---------------------------------------------------------------- CA03 — peso inválido

    @Test
    @DisplayName("US01 - CA03: Peso zero ou negativo deve lançar exceção e o saldo do usuário permanece igual")
    void deveManterSaldoIgualQuandoPesoInvalido() {
        // GIVEN
        Usuario usuario = usuarioComSaldo(1000);
        // lenient: o serviço valida o peso ANTES de buscar o usuário, então o stub pode não ser usado
        lenient().when(repositorioUsuario.findById(1L)).thenReturn(Optional.of(usuario));

        // WHEN / THEN
        assertThrows(IllegalArgumentException.class, () -> servicoColeta.registrarEntregaValidada(1L, "Ponto Central", 0.0, 0));
        assertThrows(IllegalArgumentException.class, () -> servicoColeta.registrarEntregaValidada(1L, "Ponto Central", -0.1, 0));
        assertEquals(1000, usuario.getSaldoPontos());
        verify(repositorioColeta, never()).save(any());
        verify(repositorioUsuario, never()).save(any());
    }

    @Test
    @DisplayName("US01 - CA03: Peso nulo ou ID de usuário nulo devem lançar IllegalArgumentException")
    void deveRecusarPesoNuloEUsuarioNulo() {
        // WHEN / THEN
        assertThrows(IllegalArgumentException.class, () -> servicoColeta.registrarEntregaValidada(1L, "Ponto Central", null, 0));
        assertThrows(IllegalArgumentException.class, () -> servicoColeta.registrarEntregaValidada(null, "Ponto Central", 2.0, 0));
        verifyNoInteractions(repositorioColeta, repositorioUsuario);
    }
}
