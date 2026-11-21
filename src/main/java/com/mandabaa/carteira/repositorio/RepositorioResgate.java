package com.mandabaa.carteira.repositorio;

import com.mandabaa.carteira.entidade.Resgate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Acesso ao banco para resgates. */
public interface RepositorioResgate extends JpaRepository<Resgate, Long> {

    /** Histórico de resgates de um usuário, do mais recente para o mais antigo. */
    List<Resgate> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId);
}
