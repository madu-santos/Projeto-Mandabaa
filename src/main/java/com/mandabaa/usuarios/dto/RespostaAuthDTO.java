package com.mandabaa.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespostaAuthDTO {
    private Long usuarioId;
    private String nome;
    private String email;
    private String cpf;
    private Integer saldoPontos;
    private String mensagem;
}
