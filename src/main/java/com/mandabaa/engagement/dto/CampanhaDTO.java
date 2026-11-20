package com.mandabaa.engagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampanhaDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private Integer multiplicadorBonus;
    private String status; // "ATIVA" ou "EXPIRADA"
    private LocalDate dataFim;
}
