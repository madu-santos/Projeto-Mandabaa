package com.mandabaa.comum.excecao;

/** Lançada quando o usuário não tem pontos suficientes para o resgate. */
public class SaldoInsuficienteExcecao extends RuntimeException {

    public SaldoInsuficienteExcecao(String mensagem) {
        super(mensagem);
    }
}
