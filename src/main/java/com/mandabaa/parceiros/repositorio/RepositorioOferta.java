package com.mandabaa.parceiros.repositorio;

import com.mandabaa.parceiros.entidade.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Acesso ao banco para ofertas. */
public interface RepositorioOferta extends JpaRepository<Oferta, Long> {

    /** Ofertas ativas e com estoque disponível. */
    List<Oferta> findByAtivaTrueAndEstoqueGreaterThan(Integer estoqueMinimo);

    /** Todas as ofertas ativas ordenadas por custo em pontos (inclui esgotadas para exibir "Esgotado" na tela). */
    List<Oferta> findByAtivaTrueOrderByCustoPontosAsc();
}
