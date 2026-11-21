package com.mandabaa.parceiros.repositorio;

import com.mandabaa.parceiros.entidade.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Acesso ao banco para ofertas. */
public interface RepositorioOferta extends JpaRepository<Oferta, Long> {

    /** Ofertas que o usuário pode enxergar no catálogo (ativas e com estoque). */
    List<Oferta> findByAtivaTrueAndEstoqueGreaterThan(Integer estoqueMinimo);
}
