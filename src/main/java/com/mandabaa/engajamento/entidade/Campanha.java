package com.mandabaa.engajamento.entidade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Campanha temporária de recompensas (módulo "Engajamento", US07).
 * VERSÃO INICIAL do Dia 1: a Pessoa 3 (Banco de Dados) pode ajustar os campos.
 */
@Entity
@Table(name = "campanha")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Campanha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    private String descricao;

    /** Regras e condições aplicáveis à campanha (US07 - CA03). */
    private String condicoes;

    /** Ex.: 2.0 = pontos em dobro. */
    @Column(name = "multiplicador_bonus", nullable = false)
    private Double multiplicadorBonus;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;
}
