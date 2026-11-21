package com.mandabaa.comum.excecao;

/** Lançada quando um usuário, oferta ou registro buscado não existe no banco. */
public class RecursoNaoEncontradoExcecao extends RuntimeException {

    public RecursoNaoEncontradoExcecao(String mensagem) {
        super(mensagem);
    }
}
