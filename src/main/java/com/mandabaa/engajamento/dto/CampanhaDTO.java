package com.mandabaa.engajamento.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/** Campanha como o frontend vai receber (US07). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampanhaDTO {

    private Long id;
    private String titulo;
    private String descricao;
    private String condicoes;
    private Double multiplicadorBonus;

    /** "ATIVA" ou "EXPIRADA". */
    private String situacao;

    private LocalDate dataInicio;
    private LocalDate dataFim;
}
