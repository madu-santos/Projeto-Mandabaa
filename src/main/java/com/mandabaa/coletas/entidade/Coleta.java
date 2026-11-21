package com.mandabaa.coletas.entidade;

import com.mandabaa.usuarios.entidade.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entrega validada de garrafas PET realizada por um usuário (módulo "Coletas", US01).
 * VERSÃO INICIAL do Dia 1: a Pessoa 3 (Banco de Dados) pode ajustar os campos.
 */
@Entity
@Table(name = "coleta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    /** Nome do ponto de coleta onde a entrega foi validada (ex.: "Cooperativa Verde"). */
    @Column(name = "ponto_coleta", nullable = false)
    private String pontoColeta;

    /** Data em que a entrega foi realizada e validada (US01 - CA02). */
    @Column(name = "data_entrega", nullable = false)
    private LocalDate dataEntrega;

    /** Peso de PET entregue em quilogramas (ex.: 2.5 kg). */
    @Column(name = "peso_kg", nullable = false)
    private Double pesoKg;

    /** Pontos base recebidos pela entrega (ex.: 50 pontos por kg). */
    @Column(name = "pontos_recebidos", nullable = false)
    private Integer pontosRecebidos;

    /** Pontos de bônus concedidos, quando houver (0 quando não houver). */
    @Column(name = "bonus_recebido", nullable = false)
    @Builder.Default
    private Integer bonusRecebido = 0;

    /** Indica se a entrega já foi validada (US01 - CA01). */
    @Column(nullable = false)
    @Builder.Default
    private Boolean validada = true;
}
