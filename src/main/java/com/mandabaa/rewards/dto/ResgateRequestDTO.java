package com.mandabaa.rewards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResgateRequestDTO {
    private Long usuarioId;
    private Long ofertaId;
    private String tipo; // "CUPOM" ou "CASHBACK"
    private String chavePix; // Opcional, para cashback
}
