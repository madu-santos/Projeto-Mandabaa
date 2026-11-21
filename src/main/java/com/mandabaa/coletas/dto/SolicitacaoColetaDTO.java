package com.mandabaa.coletas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Dados enviados para registrar uma nova entrega validada de PET (US01). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoColetaDTO {

    private Long usuarioId;
    private String pontoColeta;
    private Double pesoKg;
    private Integer bonus;
}
