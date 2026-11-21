package com.mandabaa.coletas.controlador;

import com.mandabaa.coletas.dto.ColetaDTO;
import com.mandabaa.coletas.dto.HistoricoColetasDTO;
import com.mandabaa.coletas.dto.SolicitacaoColetaDTO;
import com.mandabaa.coletas.servico.ServicoColeta;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST das Coletas e Histórico de Entregas de PET (US01).
 * Apenas recebe as requisições HTTP e delega as regras ao ServicoColeta.
 */
@RestController
@RequestMapping("/api/coletas")
@RequiredArgsConstructor
public class ControladorColeta {

    private final ServicoColeta servicoColeta;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<HistoricoColetasDTO> consultarHistorico(@PathVariable Long usuarioId) {
        HistoricoColetasDTO historico = servicoColeta.consultarHistorico(usuarioId);
        return ResponseEntity.ok(historico);
    }

    @PostMapping
    public ResponseEntity<ColetaDTO> registrarEntrega(@RequestBody SolicitacaoColetaDTO solicitacao) {
        ColetaDTO coletaSalva = servicoColeta.registrarEntregaValidada(
                solicitacao.getUsuarioId(),
                solicitacao.getPontoColeta(),
                solicitacao.getPesoKg(),
                solicitacao.getBonus()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(coletaSalva);
    }
}
