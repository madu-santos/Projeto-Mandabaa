package com.mandabaa.parceiros.entidade;

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

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Benefício oferecido por uma empresa parceira (módulo "Parceiros e Ofertas").
 * No MVP as ofertas entram no banco já cadastradas; a área de cadastro pelas empresas é a US04.
 * VERSÃO INICIAL do Dia 1: a Pessoa 3 (Banco de Dados) pode ajustar os campos.
 */
@Entity
@Table(name = "oferta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String parceiro;

    @Column(nullable = false)
    private String titulo;

    private String descricao;

    /** Regras e condições de uso exibidas antes da confirmação (US03 - CA01 e CA12). */
    private String condicoes;

    /** "CUPOM" ou "CASHBACK". */
    @Column(nullable = false)
    private String tipo;

    /** Valor em reais quando a oferta for de cashback (US03 - CA12). */
    @Column(name = "valor_cashback")
    private BigDecimal valorCashback;

    @Column(name = "custo_pontos", nullable = false)
    private Integer custoPontos;

    /** Quantidade disponível para resgate. */
    @Column(nullable = false)
    private Integer estoque;

    /** Data limite para resgatar esta oferta (US03 - CA04). */
    private LocalDate validade;

    @Column(nullable = false)
    private Boolean ativa;
}
