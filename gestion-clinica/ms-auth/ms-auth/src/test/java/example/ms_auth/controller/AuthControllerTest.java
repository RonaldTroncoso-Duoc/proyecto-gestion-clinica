package example.ms_auth.controller;

import example.ms_auth.dto.response.AuthResponseDTO;
import example.ms_auth.dto.response.UserProfileDTO;
import example.ms_auth.dto.response.UserResponseDTO;
import example.ms_auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AuthController controller = new AuthController(authService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void login_debeRetornarOkConToken() throws Exception {
        // ARRANGE: preparar datos y mocks.
        String requestJson = """
                {
                  "username": "admin",
                  "password": "password123"
                }
                """;

        AuthResponseDTO authResponse = AuthResponseDTO.builder()
                .token("jwt-token-test")
                .username("admin")
                .roles(List.of("ADMIN"))
                .build();

        when(authService.login(any())).thenReturn(authResponse);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login exitoso"))
                .andExpect(jsonPath("$.data.token").value("jwt-token-test"))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.roles[0]").value("ADMIN"));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(authService).login(any());

        // Un ejemplo de falla posible.
        // Se esperaba: HTTP 200 OK.
        // Se obtuvo: HTTP 401 Unauthorized.
        // QA deberia reportarlo asi: El endpoint POST /api/auth/login no permite iniciar sesion con credenciales validas.
        // Desarrollo deberia revisar: AuthController.login() y AuthService.login().
    }

    @Test
    void register_debeRetornarCreatedConUsuarioRegistrado() throws Exception {
        // ARRANGE: preparar datos y mocks.
        String requestJson = """
                {
                  "username": "usuario.test",
                  "email": "usuario.test@test.cl",
                  "password": "password123"
                }
                """;

        UserResponseDTO userResponse = UserResponseDTO.builder()
                .id(1L)
                .username("usuario.test")
                .email("usuario.test@test.cl")
                .build();

        when(authService.register(any())).thenReturn(userResponse);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuario registrado correctamente"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.username").value("usuario.test"))
                .andExpect(jsonPath("$.data.email").value("usuario.test@test.cl"));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(authService).register(any());

        // Un ejemplo de falla posible.
        // Se esperaba: HTTP 201 Created.
        // Se obtuvo: HTTP 200 OK.
        // QA deberia reportarlo asi: El endpoint POST /api/auth/register registra el usuario pero responde un estado incorrecto.
        // Desarrollo deberia revisar: AuthController.register() y el ResponseEntity con HttpStatus.CREATED.
    }

    @Test
    void registerInternal_debeRetornarCreatedConUsuarioRegistrado() throws Exception {
        // ARRANGE: preparar datos y mocks.
        String requestJson = """
                {
                  "username": "doctor.test",
                  "email": "doctor.test@test.cl",
                  "password": "password123"
                }
                """;

        UserResponseDTO userResponse = UserResponseDTO.builder()
                .id(2L)
                .username("doctor.test")
                .email("doctor.test@test.cl")
                .build();

        when(authService.registerUser(any(), org.mockito.ArgumentMatchers.eq("DOCTOR"))).thenReturn(userResponse);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(post("/api/auth/internal/register")
                        .param("role", "DOCTOR")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.username").value("doctor.test"))
                .andExpect(jsonPath("$.email").value("doctor.test@test.cl"));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(authService).registerUser(any(), org.mockito.ArgumentMatchers.eq("DOCTOR"));

        // Un ejemplo de falla posible.
        // Se esperaba: HTTP 201 Created.
        // Se obtuvo: HTTP 400 Bad Request.
        // QA deberia reportarlo asi: El endpoint POST /api/auth/internal/register no permite registrar un usuario interno con rol valido.
        // Desarrollo deberia revisar: AuthController.registerInternal() y AuthService.registerUser().
    }

    @Test
    void profile_debeRetornarOkConPerfilDelUsuarioAutenticado() throws Exception {
        // ARRANGE: preparar datos y mocks.
        UserProfileDTO profile = UserProfileDTO.builder()
                .username("admin")
                .email("admin@test.cl")
                .roles(List.of("ADMIN"))
                .build();

        when(authService.getProfile("admin")).thenReturn(profile);

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(get("/api/auth/me")
                        .principal(new TestingAuthenticationToken("admin", "password", "ROLE_ADMIN")))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Perfil obtenido correctamente"))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.email").value("admin@test.cl"))
                .andExpect(jsonPath("$.data.roles[0]").value("ADMIN"));

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(authService).getProfile("admin");

        // Un ejemplo de falla posible.
        // Se esperaba: HTTP 200 OK.
        // Se obtuvo: HTTP 500 Internal Server Error.
        // QA deberia reportarlo asi: El endpoint GET /api/auth/me falla al obtener el perfil de un usuario autenticado.
        // Desarrollo deberia revisar: AuthController.profile(), Authentication.getName() y AuthService.getProfile().
    }

    @Test
    void changePassword_debeRetornarOkCuandoActualizaPassword() throws Exception {
        // ARRANGE: preparar datos y mocks.
        String requestJson = """
                {
                  "currentPassword": "password123",
                  "newPassword": "newPassword123"
                }
                """;

        doNothing().when(authService).changePassword(org.mockito.ArgumentMatchers.eq("admin"), any());

        // ACT: ejecutar método o endpoint.
        mockMvc.perform(put("/api/auth/change-password")
                        .principal(new TestingAuthenticationToken("admin", "password", "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // ASSERT: verificar resultado esperado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Contraseña actualizada correctamente"))
                .andExpect(jsonPath("$.data").doesNotExist());

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(authService).changePassword(org.mockito.ArgumentMatchers.eq("admin"), any());

        // Un ejemplo de falla posible.
        // Se esperaba: HTTP 200 OK.
        // Se obtuvo: HTTP 400 Bad Request.
        // QA deberia reportarlo asi: El endpoint PUT /api/auth/change-password rechaza una solicitud valida de cambio de contraseña.
        // Desarrollo deberia revisar: AuthController.changePassword(), Authentication.getName() y AuthService.changePassword().
    }
}
