package com.mandabaa.coletas.repositorio;

import com.mandabaa.coletas.entidade.Coleta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Acesso ao banco para entregas de PET (US01). */
public interface RepositorioColeta extends JpaRepository<Coleta, Long> {

    /** Lista apenas as entregas validadas do usuário, da mais recente para a mais antiga. */
    List<Coleta> findByUsuarioIdAndValidadaTrueOrderByDataEntregaDesc(Long usuarioId);
}
