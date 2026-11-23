package com.mandabaa.usuarios.repositorio;

import com.mandabaa.usuarios.entidade.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/** Acesso ao banco para usuários. O Spring cria a implementação sozinho. */
public interface RepositorioUsuario extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByEmailOrCpf(String email, String cpf);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
}
