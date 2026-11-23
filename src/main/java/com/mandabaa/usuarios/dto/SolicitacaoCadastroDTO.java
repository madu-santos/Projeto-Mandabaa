package com.mandabaa.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoCadastroDTO {
    private String nome;
    private String email;
    private String cpf;
    private String senha;
}
