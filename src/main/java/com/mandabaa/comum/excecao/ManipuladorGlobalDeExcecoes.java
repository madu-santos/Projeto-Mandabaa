package com.mandabaa.comum.excecao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Captura as exceções de negócio em UM lugar só e devolve sempre o mesmo formato JSON:
 * {"status": "ERRO", "mensagem": "..."}.
 */
@Slf4j
@RestControllerAdvice
public class ManipuladorGlobalDeExcecoes {

    @ExceptionHandler(SaldoInsuficienteExcecao.class)
    public ResponseEntity<Map<String, Object>> tratarSaldoInsuficiente(SaldoInsuficienteExcecao excecao) {
        return respostaDeErro(HttpStatus.BAD_REQUEST, excecao.getMessage());
    }

    @ExceptionHandler(OfertaIndisponivelExcecao.class)
    public ResponseEntity<Map<String, Object>> tratarOfertaIndisponivel(OfertaIndisponivelExcecao excecao) {
        return respostaDeErro(HttpStatus.BAD_REQUEST, excecao.getMessage());
    }

    @ExceptionHandler(RecursoNaoEncontradoExcecao.class)
    public ResponseEntity<Map<String, Object>> tratarNaoEncontrado(RecursoNaoEncontradoExcecao excecao) {
        return respostaDeErro(HttpStatus.NOT_FOUND, excecao.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> tratarErroInesperado(Exception excecao) {
        log.error("Erro inesperado no servidor", excecao);
        return respostaDeErro(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno. Tente novamente em instantes.");
    }

    private ResponseEntity<Map<String, Object>> respostaDeErro(HttpStatus statusHttp, String mensagem) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("status", "ERRO");
        corpo.put("mensagem", mensagem);
        return ResponseEntity.status(statusHttp).body(corpo);
    }
}
