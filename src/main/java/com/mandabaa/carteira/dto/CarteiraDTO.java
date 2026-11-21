package com.mandabaa.carteira.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Informações exibidas no painel principal da carteira do usuário (US05). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarteiraDTO {

    private Long usuarioId;
    private String nomeUsuario;
    private Integer saldoPontos;
    private BigDecimal equivalenteReais;

    /** Explicação transparente da regra de cálculo e conversão (US05 - CA03 e CA04). */
    private String regraCalculo;

    /** True quando saldoPontos == 0, para o frontend exibir aviso e convite à coleta (US05 - CA07). */
    private Boolean semSaldo;

    private String mensagemSaldo;
}
