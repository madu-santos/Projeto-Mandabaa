package com.mandabaa.usuarios.controlador;

import com.mandabaa.usuarios.dto.RespostaAuthDTO;
import com.mandabaa.usuarios.dto.SolicitacaoCadastroDTO;
import com.mandabaa.usuarios.dto.SolicitacaoLoginDTO;
import com.mandabaa.usuarios.servico.ServicoAutenticacao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class ControladorAuth {

    private final ServicoAutenticacao servicoAutenticacao;

    public ControladorAuth(ServicoAutenticacao servicoAutenticacao) {
        this.servicoAutenticacao = servicoAutenticacao;
    }

    @PostMapping("/login")
    public ResponseEntity<RespostaAuthDTO> login(@RequestBody SolicitacaoLoginDTO solicitacao) {
        RespostaAuthDTO resposta = servicoAutenticacao.autenticar(solicitacao);
        return ResponseEntity.ok(resposta);
    }

    @PostMapping("/cadastro")
    public ResponseEntity<RespostaAuthDTO> cadastro(@RequestBody SolicitacaoCadastroDTO solicitacao) {
        RespostaAuthDTO resposta = servicoAutenticacao.cadastrar(solicitacao);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }
}
