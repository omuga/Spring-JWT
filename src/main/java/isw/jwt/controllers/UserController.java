package isw.jwt.controllers;

import java.util.Date;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import isw.jwt.models.Usuario;
import isw.jwt.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UserController {

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
	private final String PREFIX = "Bearer ";

    @Autowired
    @Qualifier("UsuarioService")
    private UsuarioService usuarioService;

    @GetMapping("")
    public Iterable<Usuario> getUsuarios() {
        return usuarioService.listAllUsuarios();
    }
  
    @PostMapping("")
    public ResponseEntity<String> createUsuario(@RequestParam("nombre") String nombre, 
                @RequestParam("email") String email, @RequestParam("password") String password) {
                    Usuario usuarioExistente = usuarioService.findUsuarioByEmail(email);
                    if (usuarioExistente == null) {
                    Usuario usuario = new Usuario();
                    usuario.setNombre(nombre);
                    usuario.setEmail(email);
                    usuario.setPassword(usuarioService.hashPassword(password));
                    usuarioService.registerUsuario(usuario);
        return new ResponseEntity<String>("Usuario creado satisfactoriamente", HttpStatus.OK);
                    } else {
                        return new ResponseEntity<String>("Email ya existente", HttpStatus.BAD_REQUEST);
                    }
    }

    @GetMapping("/login")
    public ResponseEntity<String> loginUsuario(@RequestParam("email") String email,
            @RequestParam("password") String password) {
        Usuario usuarioExistente = usuarioService.findUsuarioByEmail(email);
        if (usuarioExistente != null) {
            if (usuarioService.checkPassword(password, usuarioExistente.getPassword())) {
                String tokenJWT = GenerateToken(email);
                return new ResponseEntity<String>(tokenJWT, HttpStatus.OK);
            } else {
                return new ResponseEntity<String>("Contraseña Incorrecta", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<String>("Datos / Contraseña Incorrecta", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/resource")
    public ResponseEntity<String> validateJWT(@RequestHeader (name="Authorization") String token){
        if (token == null || !token.startsWith(PREFIX)){
            return new ResponseEntity<String>("Token Inexistente", HttpStatus.BAD_REQUEST);
        } else {
            String jwtToken = token.replace(PREFIX, "");
            String email = decodeJWT(jwtToken);
            return new ResponseEntity<String>(email, HttpStatus.BAD_REQUEST);       
        }      
    }

    private String GenerateToken(String email) {
        return Jwts.builder()
        .setSubject("AyudantiaJWT")
        .claim("email",email)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
        .signWith(SignatureAlgorithm.HS512, "ayudantia").compact();
    }

    private static String decodeJWT(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary("ayudantia"))
                .parseClaimsJws(jwt).getBody();
        return (String) claims.get("email");
    }
}