package com.mandabaa.parceiros.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Oferta formatada para exibição no catálogo do frontend (US03 - CA01 e CA12). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfertaDTO {

    private Long id;
    private String parceiro;
    private String titulo;
    private String descricao;
    private String condicoes;
    private String tipo;
    private BigDecimal valorCashback;
    private Integer custoPontos;
    private Integer estoque;
    private LocalDate validade;
    private Boolean ativa;
}
