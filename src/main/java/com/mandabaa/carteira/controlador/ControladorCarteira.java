package com.mandabaa.carteira.controlador;

import com.mandabaa.carteira.dto.CarteiraDTO;
import com.mandabaa.carteira.servico.ServicoCarteira;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST da Carteira e Saldo (US05).
 * Recebe o pedido do frontend e delega a regra de negócio para o ServicoCarteira.
 */
@RestController
@RequestMapping("/api/carteira")
@RequiredArgsConstructor
public class ControladorCarteira {

    private final ServicoCarteira servicoCarteira;

    @GetMapping("/{usuarioId}")
    public ResponseEntity<CarteiraDTO> consultarCarteira(@PathVariable Long usuarioId) {
        CarteiraDTO carteira = servicoCarteira.consultarCarteira(usuarioId);
        return ResponseEntity.ok(carteira);
    }
}
