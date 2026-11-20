package com.mandabaa.rewards.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResgateResponseDTO {
    private Long resgateId;
    private String status; // "CONCLUIDO", "PENDENTE"
    private String codigoVoucher;
    private Integer novoSaldo;
    private String mensagem;
}
