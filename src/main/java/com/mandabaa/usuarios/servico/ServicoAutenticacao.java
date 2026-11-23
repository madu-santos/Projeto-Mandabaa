package com.mandabaa.usuarios.servico;

import com.mandabaa.comum.excecao.RecursoNaoEncontradoExcecao;
import com.mandabaa.usuarios.dto.RespostaAuthDTO;
import com.mandabaa.usuarios.dto.SolicitacaoCadastroDTO;
import com.mandabaa.usuarios.dto.SolicitacaoLoginDTO;
import com.mandabaa.usuarios.entidade.Usuario;
import com.mandabaa.usuarios.repositorio.RepositorioUsuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicoAutenticacao {

    private final RepositorioUsuario repositorioUsuario;

    public ServicoAutenticacao(RepositorioUsuario repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    @Transactional(readOnly = true)
    public RespostaAuthDTO autenticar(SolicitacaoLoginDTO solicitacao) {
        if (solicitacao == null || solicitacao.getLogin() == null || solicitacao.getLogin().isBlank()) {
            throw new IllegalArgumentException("E-mail ou CPF é obrigatório para login.");
        }
        if (solicitacao.getSenha() == null || solicitacao.getSenha().isBlank()) {
            throw new IllegalArgumentException("Senha é obrigatória.");
        }

        String loginLimpo = solicitacao.getLogin().trim().toLowerCase();
        Usuario usuario = repositorioUsuario.findByEmail(loginLimpo)
                .or(() -> repositorioUsuario.findByEmailOrCpf(loginLimpo, solicitacao.getLogin().trim()))
                .orElseThrow(() -> new RecursoNaoEncontradoExcecao("Usuário não encontrado com as credenciais fornecidas."));

        if (usuario.getSenha() != null && !usuario.getSenha().equals(solicitacao.getSenha().trim())) {
            throw new IllegalArgumentException("Senha incorreta.");
        }

        return RespostaAuthDTO.builder()
                .usuarioId(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .cpf(usuario.getCpf())
                .saldoPontos(usuario.getSaldoPontos())
                .mensagem("Autenticação realizada com sucesso!")
                .build();
    }

    @Transactional
    public RespostaAuthDTO cadastrar(SolicitacaoCadastroDTO solicitacao) {
        if (solicitacao == null) {
            throw new IllegalArgumentException("Dados de cadastro não informados.");
        }
        if (solicitacao.getNome() == null || solicitacao.getNome().trim().length() < 3) {
            throw new IllegalArgumentException("Nome completo deve ter pelo menos 3 caracteres.");
        }
        if (solicitacao.getEmail() == null || !solicitacao.getEmail().contains("@")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        if (solicitacao.getSenha() == null || solicitacao.getSenha().trim().length() < 4) {
            throw new IllegalArgumentException("Senha deve ter pelo menos 4 caracteres.");
        }

        String emailLimpo = solicitacao.getEmail().trim().toLowerCase();
        if (repositorioUsuario.existsByEmail(emailLimpo)) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com este e-mail.");
        }

        String cpfLimpo = solicitacao.getCpf() != null ? solicitacao.getCpf().replaceAll("[^0-9]", "") : null;
        if (cpfLimpo != null && !cpfLimpo.isBlank() && repositorioUsuario.existsByCpf(cpfLimpo)) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com este CPF.");
        }

        Usuario novoUsuario = Usuario.builder()
                .nome(solicitacao.getNome().trim())
                .email(emailLimpo)
                .cpf(cpfLimpo)
                .senha(solicitacao.getSenha().trim())
                .saldoPontos(0)
                .build();

        novoUsuario = repositorioUsuario.save(novoUsuario);

        return RespostaAuthDTO.builder()
                .usuarioId(novoUsuario.getId())
                .nome(novoUsuario.getNome())
                .email(novoUsuario.getEmail())
                .cpf(novoUsuario.getCpf())
                .saldoPontos(novoUsuario.getSaldoPontos())
                .mensagem("Cadastro realizado com sucesso! Bem-vindo(a) ao Mandabaa.")
                .build();
    }
}
