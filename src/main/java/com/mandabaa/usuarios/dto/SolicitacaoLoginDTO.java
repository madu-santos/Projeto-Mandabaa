package com.mandabaa.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoLoginDTO {
    private String login; // pode ser email ou CPF
    private String senha;
}
