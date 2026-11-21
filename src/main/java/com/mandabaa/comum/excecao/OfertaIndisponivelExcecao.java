package com.mandabaa.comum.excecao;

/** Lançada quando a oferta está esgotada, inativa, vencida ou não existe. */
public class OfertaIndisponivelExcecao extends RuntimeException {

    public OfertaIndisponivelExcecao(String mensagem) {
        super(mensagem);
    }
}
