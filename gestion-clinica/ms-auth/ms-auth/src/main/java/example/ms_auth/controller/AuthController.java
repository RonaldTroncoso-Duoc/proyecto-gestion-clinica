package example.ms_auth.controller;

import example.ms_auth.model.Usuario;
import example.ms_auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Registro de usuario (mantiene el retorno del objeto Usuario)
    @PostMapping("/register")
    public ResponseEntity<Usuario> registrar(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(authService.registrar(usuario));
    }

    // Login de usuario: Ahora devuelve un JSON con el token
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        try {
            String token = authService.login(username, password);
            
            // Creamos un mapa para devolver el token de forma estructurada
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Si las credenciales fallan, devolvemos 401
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}