package com.mandabaa.carteira.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Resposta que o backend devolve ao frontend depois de um resgate (US03). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespostaResgateDTO {

    private Long resgateId;

    /**
     * Cupom: "DISPONIVEL", "UTILIZADO" ou "EXPIRADO".
     * Cashback: "PAGAMENTO_PENDENTE" ou "PAGAMENTO_CONFIRMADO".
     */
    private String situacao;

    private String codigoComprovante;
    private Integer novoSaldo;
    private String mensagem;
}
