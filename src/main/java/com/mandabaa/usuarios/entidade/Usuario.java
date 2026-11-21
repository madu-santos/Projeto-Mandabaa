package com.mandabaa.usuarios.entidade;

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

/**
 * Pessoa que entrega PET e acumula pontos (módulo "Usuários e Acesso").
 * VERSÃO INICIAL do Dia 1: a Pessoa 3 (Banco de Dados) pode ajustar os campos.
 */
@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    /** Pontos disponíveis para troca por benefícios (US03 e US05). */
    @Column(name = "saldo_pontos", nullable = false)
    private Integer saldoPontos;

    /** Preferência de recebimento de avisos sobre campanhas temporárias (US07). */
    @Column(name = "receber_notificacoes", nullable = false)
    @Builder.Default
    private Boolean receberNotificacoes = true;
}
