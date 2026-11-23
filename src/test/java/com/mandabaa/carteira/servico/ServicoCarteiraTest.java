package com.mandabaa.carteira.servico;

import com.mandabaa.carteira.dto.CarteiraDTO;
import com.mandabaa.comum.excecao.RecursoNaoEncontradoExcecao;
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
import static org.mockito.Mockito.*;

/**
 * Testes Unitários da US05 — Consulta de Saldo e Carteira (Sprint 5.1).
 * FASE RED (TDD): gerado com base estrita nos Critérios de Aceitação Given-When-Then.
 */
@ExtendWith(MockitoExtension.class)
class ServicoCarteiraTest {

    @Mock
    private RepositorioUsuario repositorioUsuario;

    @InjectMocks
    private ServicoCarteira servicoCarteira;

    @Test
    @DisplayName("US05 - CA01: Deve consultar carteira com saldo e calcular equivalente em reais corretamente")
    void deveConsultarCarteiraComSaldoECalcularEquivalenteEmReais() {
        // GIVEN (Dado / Arrange)
        Long usuarioId = 1L;
        Usuario usuario = Usuario.builder()
                .id(usuarioId)
                .nome("Alex Silva")
                .saldoPontos(1250)
                .build();

        when(repositorioUsuario.findById(usuarioId)).thenReturn(Optional.of(usuario));

        // WHEN (Quando / Act)
        CarteiraDTO resultado = servicoCarteira.consultarCarteira(usuarioId);

        // THEN (Então / Assert)
        assertNotNull(resultado);
        assertEquals(usuarioId, resultado.getUsuarioId());
        assertEquals("Alex Silva", resultado.getNomeUsuario());
        assertEquals(1250, resultado.getSaldoPontos());
        assertEquals(new BigDecimal("25.00"), resultado.getEquivalenteReais());
        assertFalse(resultado.getSemSaldo());
        assertNotNull(resultado.getRegraCalculo());
        assertTrue(resultado.getRegraCalculo().contains("100 pontos = R$ 2,00") || resultado.getRegraCalculo().contains("50 pontos"));
        verify(repositorioUsuario, times(1)).findById(usuarioId);
    }

    @Test
    @DisplayName("US05 - CA02: Deve consultar carteira sem saldo e exibir aviso para entregar PET")
    void deveConsultarCarteiraSemSaldoEExibirAviso() {
        // GIVEN (Dado / Arrange)
        Long usuarioId = 2L;
        Usuario usuario = Usuario.builder()
                .id(usuarioId)
                .nome("Maria Souza")
                .saldoPontos(0)
                .build();

        when(repositorioUsuario.findById(usuarioId)).thenReturn(Optional.of(usuario));

        // WHEN (Quando / Act)
        CarteiraDTO resultado = servicoCarteira.consultarCarteira(usuarioId);

        // THEN (Então / Assert)
        assertNotNull(resultado);
        assertEquals(usuarioId, resultado.getUsuarioId());
        assertEquals(0, resultado.getSaldoPontos());
        assertEquals(new BigDecimal("0.00"), resultado.getEquivalenteReais());
        assertTrue(resultado.getSemSaldo());
        assertNotNull(resultado.getMensagemSaldo());
        verify(repositorioUsuario, times(1)).findById(usuarioId);
    }

    @Test
    @DisplayName("US05 - CA03: Deve lançar RecursoNaoEncontradoExcecao quando usuário não existir")
    void deveLancarExcecaoQuandoUsuarioNaoExistir() {
        // GIVEN (Dado / Arrange)
        Long usuarioInexistenteId = 999L;
        when(repositorioUsuario.findById(usuarioInexistenteId)).thenReturn(Optional.empty());

        // WHEN / THEN (Quando e Então)
        assertThrows(RecursoNaoEncontradoExcecao.class, () -> {
            servicoCarteira.consultarCarteira(usuarioInexistenteId);
        });
        verify(repositorioUsuario, times(1)).findById(usuarioInexistenteId);
    }
}

