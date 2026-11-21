package com.mandabaa.coletas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/** Um registro individual de entrega de PET exibido no histórico (US01 - CA02). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColetaDTO {

    private Long id;
    private String pontoColeta;
    private LocalDate dataEntrega;
    private Double pesoKg;
    private Integer pontosRecebidos;
    private Integer bonusRecebido;
}
