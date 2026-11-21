package com.mandabaa.carteira.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Dados que o frontend envia quando o usuário confirma um resgate. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoResgateDTO {

    private Long usuarioId;
    private Long ofertaId;

    /** "CUPOM" ou "CASHBACK". */
    private String tipo;

    /** Só usado quando o tipo for CASHBACK (forma de recebimento). */
    private String chavePix;
}
