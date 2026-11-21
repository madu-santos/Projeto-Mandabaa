package com.mandabaa.coletas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Histórico completo de entregas do usuário + soma total de PET entregue (US01 - CA03 e CA05). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoColetasDTO {

    private Long usuarioId;
    private Double totalPetKg;
    private Integer totalPontosRecebidos;
    private Integer totalBonusRecebidos;
    private List<ColetaDTO> coletas;
}
