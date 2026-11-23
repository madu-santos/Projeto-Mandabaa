package com.mandabaa.usuarios.servico;

import com.mandabaa.comum.excecao.RecursoNaoEncontradoExcecao;
import com.mandabaa.usuarios.dto.RespostaAuthDTO;
import com.mandabaa.usuarios.dto.SolicitacaoCadastroDTO;
import com.mandabaa.usuarios.dto.SolicitacaoLoginDTO;
import com.mandabaa.usuarios.entidade.Usuario;
import com.mandabaa.usuarios.repositorio.RepositorioUsuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoAutenticacaoTest {

    @Mock
    private RepositorioUsuario repositorioUsuario;

    @InjectMocks
    private ServicoAutenticacao servicoAutenticacao;

    @Test
    @DisplayName("Deve autenticar usuário com sucesso quando credenciais estiverem corretas")
    void deveAutenticarComSucesso() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("Alex Silva")
                .email("alex@mandabaa.com")
                .senha("123456")
                .saldoPontos(1250)
                .build();

        when(repositorioUsuario.findByEmail("alex@mandabaa.com")).thenReturn(Optional.of(usuario));

        RespostaAuthDTO resposta = servicoAutenticacao.autenticar(new SolicitacaoLoginDTO("alex@mandabaa.com", "123456"));

        assertNotNull(resposta);
        assertEquals(1L, resposta.getUsuarioId());
        assertEquals("Alex Silva", resposta.getNome());
        assertEquals(1250, resposta.getSaldoPontos());
    }

    @Test
    @DisplayName("Deve falhar autenticação quando senha for incorreta")
    void deveFalharQuandoSenhaIncorreta() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("Alex Silva")
                .email("alex@mandabaa.com")
                .senha("123456")
                .saldoPontos(1250)
                .build();

        when(repositorioUsuario.findByEmail("alex@mandabaa.com")).thenReturn(Optional.of(usuario));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                servicoAutenticacao.autenticar(new SolicitacaoLoginDTO("alex@mandabaa.com", "errada")));

        assertEquals("Senha incorreta.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar autenticação quando usuário não existir")
    void deveFalharQuandoUsuarioNaoExiste() {
        when(repositorioUsuario.findByEmail("naoexiste@mandabaa.com")).thenReturn(Optional.empty());
        when(repositorioUsuario.findByEmailOrCpf("naoexiste@mandabaa.com", "naoexiste@mandabaa.com")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoExcecao.class, () ->
                servicoAutenticacao.autenticar(new SolicitacaoLoginDTO("naoexiste@mandabaa.com", "123456")));
    }

    @Test
    @DisplayName("Deve cadastrar novo usuário com saldo zero")
    void deveCadastrarNovoUsuarioComSaldoZero() {
        when(repositorioUsuario.existsByEmail("novo@mandabaa.com")).thenReturn(false);
        when(repositorioUsuario.save(any(Usuario.class))).thenAnswer(i -> {
            Usuario u = i.getArgument(0);
            u.setId(99L);
            return u;
        });

        SolicitacaoCadastroDTO solicitacao = new SolicitacaoCadastroDTO("Novo Usuario", "novo@mandabaa.com", "11122233344", "senha123");
        RespostaAuthDTO resposta = servicoAutenticacao.cadastrar(solicitacao);

        assertNotNull(resposta);
        assertEquals(99L, resposta.getUsuarioId());
        assertEquals("Novo Usuario", resposta.getNome());
        assertEquals(0, resposta.getSaldoPontos());
        verify(repositorioUsuario).save(any(Usuario.class));
    }
}
