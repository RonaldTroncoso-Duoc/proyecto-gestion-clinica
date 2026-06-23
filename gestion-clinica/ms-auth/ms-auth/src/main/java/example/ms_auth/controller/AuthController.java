package example.ms_auth.controller;

import example.ms_auth.dto.request.ChangePasswordDTO;
import example.ms_auth.dto.request.LoginRequestDTO;
import example.ms_auth.dto.request.RegisterRequestDTO;
import example.ms_auth.dto.response.ApiResponseDTO;
import example.ms_auth.dto.response.AuthResponseDTO;
import example.ms_auth.dto.response.UserResponseDTO;
import example.ms_auth.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import example.ms_auth.dto.response.UserProfileDTO;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth", description = "API de autenticación")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request
    ) {

        log.info("Request login recibida para usuario {}",
                request.getUsername());

        AuthResponseDTO response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponseDTO.success(
                        "Login exitoso",
                        response
                )
        );
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> register(
            @Valid @RequestBody RegisterRequestDTO request
    ) {

        log.info("Request registro recibida para usuario {}",
                request.getUsername());

        UserResponseDTO response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponseDTO.success(
                                "Usuario registrado correctamente",
                                response
                        )
                );
    }

    @PostMapping("/internal/register")
    public ResponseEntity<UserResponseDTO> registerInternal(
            @Valid @RequestBody RegisterRequestDTO request,
            @RequestParam(defaultValue = "PATIENT") String role
    ) {

        log.info("Request registro interno para usuario {} con rol {}",
                request.getUsername(), role);

        UserResponseDTO response = authService.registerUser(request, role);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/me")
        public ResponseEntity<ApiResponseDTO<UserProfileDTO>> profile( 
        Authentication authentication
) {

    UserProfileDTO profile =
            authService.getProfile(authentication.getName());

    return ResponseEntity.ok(
            ApiResponseDTO.success(
                    "Perfil obtenido correctamente",
                    profile
            )
    );
}

@PutMapping("/change-password")
public ResponseEntity<ApiResponseDTO<Void>> changePassword(
        Authentication authentication,
        @Valid @RequestBody ChangePasswordDTO request
) {

    authService.changePassword(
            authentication.getName(),
            request
    );

    return ResponseEntity.ok(
            ApiResponseDTO.success(
                    "Contraseña actualizada correctamente",
                    null
            )
    );
}
}