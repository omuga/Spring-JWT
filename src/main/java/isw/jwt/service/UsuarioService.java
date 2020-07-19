package isw.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import isw.jwt.models.Usuario;
import isw.jwt.daos.UsuarioRepository;

@Service("UsuarioService")
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario registerUsuario (Usuario usuario){
        return usuarioRepository.save(usuario);
    }

    public Usuario findUsuarioById(Long id){
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario findUsuarioByEmail(String email){
        return usuarioRepository.findByEmail(email);
    }

    public Iterable<Usuario> listAllUsuarios(){
        return usuarioRepository.findAll();
    }

    public String hashPassword(String plainTextPassword){
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    public Boolean checkPassword(String plainTextPassword, String hashedPassword){
        if (BCrypt.checkpw(plainTextPassword, hashedPassword)){
            return true;
        } else {
            return false;
        }
    } 

    
}