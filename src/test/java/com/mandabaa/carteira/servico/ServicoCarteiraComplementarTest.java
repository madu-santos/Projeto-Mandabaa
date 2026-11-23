package com.mandabaa.carteira.servico;

import com.mandabaa.carteira.dto.CarteiraDTO;
import com.mandabaa.usuarios.entidade.Usuario;
import com.mandabaa.usuarios.repositorio.RepositorioUsuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes COMPLEMENTARES da US05 (Pessoa 4 — QA, Sprint 5.1).
 *
 * Reforçam os critérios do plano com verificações mais rígidas:
 *  - o teste original do CA01 aceita a regra com "||" (basta UM dos textos); aqui exigimos os DOIS;
 *  - conversão pontos -> reais em vários valores (100 pontos = R$ 2,00);
 *  - saldo nulo, ID nulo e texto exato do aviso de carteira sem saldo.
 */
@ExtendWith(MockitoExtension.class)
class ServicoCarteiraComplementarTest {

    @Mock
    private RepositorioUsuario repositorioUsuario;

    @InjectMocks
    private ServicoCarteira servicoCarteira;

    @Test
    @DisplayName("US05 - CA01: Texto da regra deve informar AS DUAS regras (50 pontos por kg e 100 pontos = R$ 2,00)")
    void deveExibirAsDuasRegrasDeCalculoNoTexto() {
        // GIVEN
        Usuario usuario = Usuario.builder().id(1L).nome("Alex").saldoPontos(1250).build();
        when(repositorioUsuario.findById(1L)).thenReturn(Optional.of(usuario));

        // WHEN
        CarteiraDTO resultado = servicoCarteira.consultarCarteira(1L);

        // THEN
        assertTrue(resultado.getRegraCalculo().contains("50 pontos"));
        assertTrue(resultado.getRegraCalculo().contains("100 pontos = R$ 2,00"));
    }

    @ParameterizedTest(name = "{0} pontos = R$ {1}")
    @CsvSource({"1,0.02", "50,1.00", "99,1.98", "100,2.00", "1250,25.00", "99999,1999.98"})
    @DisplayName("US05 - CA01: Conversão de pontos em reais usa R$ 0,02 por ponto (100 pontos = R$ 2,00)")
    void deveConverterPontosEmReaisNaRegraOficial(int pontos, String reaisEsperados) {
        // GIVEN
        Usuario usuario = Usuario.builder().id(1L).nome("Alex").saldoPontos(pontos).build();
        when(repositorioUsuario.findById(1L)).thenReturn(Optional.of(usuario));

        // WHEN
        CarteiraDTO resultado = servicoCarteira.consultarCarteira(1L);

        // THEN
        assertEquals(new BigDecimal(reaisEsperados), resultado.getEquivalenteReais());
        assertFalse(resultado.getSemSaldo());
    }

    @Test
    @DisplayName("US05 - CA02: Carteira sem saldo deve orientar o usuário a entregar PET e zerar o equivalente")
    void deveOrientarUsuarioSemSaldoAEntregarPet() {
        // GIVEN
        Usuario usuario = Usuario.builder().id(2L).nome("Maria").saldoPontos(0).build();
        when(repositorioUsuario.findById(2L)).thenReturn(Optional.of(usuario));

        // WHEN
        CarteiraDTO resultado = servicoCarteira.consultarCarteira(2L);

        // THEN
        assertTrue(resultado.getSemSaldo());
        assertEquals(0, resultado.getSaldoPontos());
        assertEquals(new BigDecimal("0.00"), resultado.getEquivalenteReais());
        assertTrue(resultado.getMensagemSaldo().contains("Entregue garrafas PET"));
        assertNotNull(resultado.getRegraCalculo());
    }

    @Test
    @DisplayName("US05 - CA02: Saldo nulo no banco deve ser tratado como zero (carteira sem saldo)")
    void deveTratarSaldoNuloComoZero() {
        // GIVEN
        Usuario usuario = Usuario.builder().id(3L).nome("Sem Saldo").saldoPontos(null).build();
        when(repositorioUsuario.findById(3L)).thenReturn(Optional.of(usuario));

        // WHEN
        CarteiraDTO resultado = servicoCarteira.consultarCarteira(3L);

        // THEN
        assertEquals(0, resultado.getSaldoPontos());
        assertTrue(resultado.getSemSaldo());
        assertEquals(new BigDecimal("0.00"), resultado.getEquivalenteReais());
    }

    @Test
    @DisplayName("US05: ID de usuário nulo deve lançar IllegalArgumentException sem consultar o banco")
    void deveRecusarIdDeUsuarioNulo() {
        // WHEN / THEN
        assertThrows(IllegalArgumentException.class, () -> servicoCarteira.consultarCarteira(null));
        verifyNoInteractions(repositorioUsuario);
    }

    @Test
    @DisplayName("US05: A consulta é somente leitura e não deve gravar nem alterar o usuário")
    void naoDeveAlterarOUsuarioNaConsulta() {
        // GIVEN
        Usuario usuario = Usuario.builder().id(1L).nome("Alex").saldoPontos(1250).build();
        when(repositorioUsuario.findById(1L)).thenReturn(Optional.of(usuario));

        // WHEN
        servicoCarteira.consultarCarteira(1L);

        // THEN
        assertEquals(1250, usuario.getSaldoPontos());
        verify(repositorioUsuario, never()).save(any());
    }
}
