package com.mandabaa.usuarios.repositorio;

import com.mandabaa.usuarios.entidade.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para usuários. O Spring cria a implementação sozinho. */
public interface RepositorioUsuario extends JpaRepository<Usuario, Long> {
}
