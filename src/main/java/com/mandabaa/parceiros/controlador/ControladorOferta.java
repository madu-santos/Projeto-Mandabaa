package com.mandabaa.parceiros.controlador;

import com.mandabaa.parceiros.dto.OfertaDTO;
import com.mandabaa.parceiros.entidade.Oferta;
import com.mandabaa.parceiros.repositorio.RepositorioOferta;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST do Catálogo de Ofertas dos Parceiros (apoio à US03).
 * Lista ofertas ativas cadastradas no banco para exibição na aba "Benefícios".
 */
@RestController
@RequestMapping("/api/ofertas")
@RequiredArgsConstructor
public class ControladorOferta {

    private final RepositorioOferta repositorioOferta;

    @GetMapping
    public ResponseEntity<List<OfertaDTO>> listarOfertas() {
        List<OfertaDTO> ofertas = repositorioOferta.findByAtivaTrueOrderByCustoPontosAsc()
                .stream()
                .map(this::converterParaDTO)
                .toList();
        return ResponseEntity.ok(ofertas);
    }

    private OfertaDTO converterParaDTO(Oferta oferta) {
        return OfertaDTO.builder()
                .id(oferta.getId())
                .parceiro(oferta.getParceiro())
                .titulo(oferta.getTitulo())
                .descricao(oferta.getDescricao())
                .condicoes(oferta.getCondicoes())
                .tipo(oferta.getTipo())
                .valorCashback(oferta.getValorCashback())
                .custoPontos(oferta.getCustoPontos())
                .estoque(oferta.getEstoque())
                .validade(oferta.getValidade())
                .ativa(oferta.getAtiva())
                .build();
    }
}
