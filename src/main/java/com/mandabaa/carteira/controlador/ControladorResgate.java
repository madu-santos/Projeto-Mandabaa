package com.mandabaa.carteira.controlador;

import com.mandabaa.carteira.dto.ItemResgateDTO;
import com.mandabaa.carteira.dto.RespostaResgateDTO;
import com.mandabaa.carteira.dto.SolicitacaoResgateDTO;
import com.mandabaa.carteira.servico.ServicoResgate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST de Resgate de Benefícios e Cashback (US03).
 * Delega todas as validações e regras de negócio para o ServicoResgate.
 */
@RestController
@RequestMapping("/api/resgates")
@RequiredArgsConstructor
public class ControladorResgate {

    private final ServicoResgate servicoResgate;

    @PostMapping
    public ResponseEntity<RespostaResgateDTO> solicitarResgate(@RequestBody SolicitacaoResgateDTO solicitacao) {
        RespostaResgateDTO resposta = servicoResgate.realizarResgate(solicitacao);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ItemResgateDTO>> listarMeusResgates(@PathVariable Long usuarioId) {
        List<ItemResgateDTO> resgates = servicoResgate.listarResgatesDoUsuario(usuarioId);
        return ResponseEntity.ok(resgates);
    }
}
