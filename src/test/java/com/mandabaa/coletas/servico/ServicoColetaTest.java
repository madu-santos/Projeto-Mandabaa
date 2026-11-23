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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes Unitários da US01 — Registro e Histórico de Coletas (Sprint 5.1).
 * FASE RED (TDD): gerado com base estrita nos Critérios de Aceitação Given-When-Then.
 */
@ExtendWith(MockitoExtension.class)
class ServicoColetaTest {

    @Mock
    private RepositorioColeta repositorioColeta;

    @Mock
    private RepositorioUsuario repositorioUsuario;

    @InjectMocks
    private ServicoColeta servicoColeta;

    @Test
    @DisplayName("US01 - CA01: Deve consultar histórico de coletas calculando o total de PET entregue")
    void deveConsultarHistoricoECalcularTotalPetEntregue() {
        // GIVEN (Dado / Arrange)
        Long usuarioId = 1L;
        Usuario usuario = Usuario.builder()
                .id(usuarioId)
                .nome("Alex Silva")
                .saldoPontos(1250)
                .build();

        Coleta coleta1 = Coleta.builder()
                .id(1L)
                .usuario(usuario)
                .pontoColeta("Cooperativa Verde")
                .dataEntrega(LocalDate.of(2026, 11, 14))
                .pesoKg(2.5)
                .pontosRecebidos(125)
                .bonusRecebido(25)
                .validada(true)
                .build();

        Coleta coleta2 = Coleta.builder()
                .id(2L)
                .usuario(usuario)
                .pontoColeta("Ecoponto Central")
                .dataEntrega(LocalDate.of(2026, 11, 9))
                .pesoKg(1.8)
                .pontosRecebidos(90)
                .bonusRecebido(0)
                .validada(true)
                .build();

        when(repositorioUsuario.existsById(usuarioId)).thenReturn(true);
        when(repositorioColeta.findByUsuarioIdAndValidadaTrueOrderByDataEntregaDesc(usuarioId))
                .thenReturn(Arrays.asList(coleta1, coleta2));

        // WHEN (Quando / Act)
        HistoricoColetasDTO resultado = servicoColeta.consultarHistorico(usuarioId);

        // THEN (Então / Assert)
        assertNotNull(resultado);
        assertEquals(usuarioId, resultado.getUsuarioId());
        assertEquals(4.3, resultado.getTotalPetKg(), 0.001);
        assertEquals(215, resultado.getTotalPontosRecebidos());
        assertEquals(25, resultado.getTotalBonusRecebidos());
        assertEquals(2, resultado.getColetas().size());

        ColetaDTO item1 = resultado.getColetas().get(0);
        assertEquals("Cooperativa Verde", item1.getPontoColeta());
        assertEquals(2.5, item1.getPesoKg());
        assertEquals(125, item1.getPontosRecebidos());
        assertEquals(25, item1.getBonusRecebido());

        verify(repositorioColeta, times(1)).findByUsuarioIdAndValidadaTrueOrderByDataEntregaDesc(usuarioId);
    }

    @Test
    @DisplayName("US01 - CA02: Deve registrar nova entrega validada e atualizar saldo do usuário")
    void deveRegistrarNovaEntregaValidadaEAtualizarSaldo() {
        // GIVEN (Dado / Arrange)
        Long usuarioId = 1L;
        Usuario usuario = Usuario.builder()
                .id(usuarioId)
                .nome("Alex Silva")
                .saldoPontos(1000)
                .build();

        when(repositorioUsuario.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(repositorioColeta.save(any(Coleta.class))).thenAnswer(invocation -> {
            Coleta c = invocation.getArgument(0);
            c.setId(10L);
            return c;
        });

        // WHEN (Quando / Act)
        // 2,0 kg a 50 pontos/kg = 100 pontos base + 0 bonus -> saldo deve ir para 1.100
        ColetaDTO resultado = servicoColeta.registrarEntregaValidada(usuarioId, "Ponto Central", 2.0, 0);

        // THEN (Então / Assert)
        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals("Ponto Central", resultado.getPontoColeta());
        assertEquals(2.0, resultado.getPesoKg());
        assertEquals(100, resultado.getPontosRecebidos());
        assertEquals(0, resultado.getBonusRecebido());

        assertEquals(1100, usuario.getSaldoPontos());
        verify(repositorioUsuario, times(1)).save(usuario);
        verify(repositorioColeta, times(1)).save(any(Coleta.class));
    }

    @Test
    @DisplayName("US01 - CA03: Deve lançar IllegalArgumentException quando peso for menor ou igual a zero")
    void deveLancarExcecaoQuandoPesoMenorOuIgualAZero() {
        // GIVEN (Dado / Arrange)
        Long usuarioId = 1L;

        // WHEN / THEN (Quando e Então)
        assertThrows(IllegalArgumentException.class, () -> {
            servicoColeta.registrarEntregaValidada(usuarioId, "Ponto Central", 0.0, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            servicoColeta.registrarEntregaValidada(usuarioId, "Ponto Central", -1.5, 0);
        });

        verify(repositorioColeta, never()).save(any());
        verify(repositorioUsuario, never()).save(any());
    }
}

