package example.ms_auth.service;

import example.ms_auth.model.Usuario;
import example.ms_auth.repository.UsuarioRepository;
import example.ms_auth.util.JwtUtils; // Importamos la utilidad
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils; // Inyectamos la fábrica de tokens

    public Usuario registrar(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public String login(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (passwordEncoder.matches(password, usuario.getPassword())) {
            // Retornamos el TOKEN en lugar de un simple mensaje
            return jwtUtils.generarToken(usuario.getUsername(), usuario.getRol());
        } else {
            throw new RuntimeException("Contraseña incorrecta");
        }
    }
}