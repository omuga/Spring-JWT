package isw.jwt.daos;

import isw.jwt.models.Usuario;
import org.springframework.data.repository.CrudRepository;


public interface UsuarioRepository extends CrudRepository<Usuario,Long> {
    Usuario findByEmail(String email);
}

