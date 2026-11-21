package com.mandabaa.engajamento.repositorio;

import com.mandabaa.engajamento.entidade.Campanha;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para campanhas. */
public interface RepositorioCampanha extends JpaRepository<Campanha, Long> {
}
