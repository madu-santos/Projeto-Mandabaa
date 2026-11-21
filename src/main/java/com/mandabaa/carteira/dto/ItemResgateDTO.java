package com.mandabaa.carteira.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Item exibido na aba "Meus Benefícios Resgatados" (US03 - CA07, CA08 e CA09). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResgateDTO {

    private Long resgateId;
    private String tituloOferta;
    private String parceiro;
    private String tipo;
    private String situacao;
    private String codigoComprovante;
    private String formaRecebimento;
    private Integer pontosDescontados;
    private LocalDateTime criadoEm;
}
