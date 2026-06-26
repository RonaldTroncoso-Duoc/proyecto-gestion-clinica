package example.ms_auth.service.impl;

import example.ms_auth.dto.request.ChangePasswordDTO;
import example.ms_auth.dto.request.RegisterRequestDTO;
import example.ms_auth.dto.request.LoginRequestDTO;
import example.ms_auth.dto.response.AuthResponseDTO;
import example.ms_auth.dto.response.UserProfileDTO;
import example.ms_auth.dto.response.UserResponseDTO;
import example.ms_auth.entity.RoleEntity;
import example.ms_auth.entity.UserEntity;
import example.ms_auth.exception.BusinessException;
import example.ms_auth.exception.ResourceNotFoundException;
import example.ms_auth.exception.UnauthorizedException;
import example.ms_auth.mapper.UserMapper;
import example.ms_auth.repository.RoleRepository;
import example.ms_auth.repository.UserRepository;
import example.ms_auth.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl service;

    @Test
    void login_debeRetornarTokenUsuarioYRoles() {
        // ARRANGE: preparar datos y mocks.
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("admin");
        request.setPassword("password123");

        RoleEntity adminRole = RoleEntity.builder()
                .id(1L)
                .name("ADMIN")
                .description("Administrador")
                .build();

        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("admin")
                .email("admin@test.cl")
                .password("encoded-password")
                .enabled(true)
                .roles(Set.of(adminRole))
                .build();

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token-test");

        // ACT: ejecutar método o endpoint.
        AuthResponseDTO resultado = service.login(request);

        // ASSERT: verificar resultado esperado.
        assertThat(resultado.getToken()).isEqualTo("jwt-token-test");
        assertThat(resultado.getUsername()).isEqualTo("admin");
        assertThat(resultado.getRoles()).containsExactly("ADMIN");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(userRepository).findByUsername("admin");
        verify(passwordEncoder).matches("password123", "encoded-password");
        verify(jwtService).generateToken(user);

        // Un ejemplo de falla posible.
        // Se esperaba: token JWT generado.
        // Se obtuvo: token nulo.
        // QA deberia reportarlo asi: El metodo login() autentica al usuario, pero no retorna un token valido.
        // Desarrollo deberia revisar: AuthServiceImpl.login(), JwtService.generateToken() y el mapeo de AuthResponseDTO.
    }

    @Test
    void register_debeRegistrarUsuarioConRolPatientYRetornarUsuarioMapeado() {
        // ARRANGE: preparar datos y mocks.
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("paciente.test");
        request.setEmail("paciente.test@test.cl");
        request.setPassword("password123");

        RoleEntity patientRole = RoleEntity.builder()
                .id(2L)
                .name("PATIENT")
                .description("Paciente")
                .build();

        UserEntity savedUser = UserEntity.builder()
                .id(2L)
                .username("paciente.test")
                .email("paciente.test@test.cl")
                .password("encoded-password")
                .enabled(true)
                .roles(Set.of(patientRole))
                .build();

        UserResponseDTO userResponse = UserResponseDTO.builder()
                .id(2L)
                .username("paciente.test")
                .email("paciente.test@test.cl")
                .build();

        when(userRepository.existsByUsername("paciente.test")).thenReturn(false);
        when(userRepository.existsByEmail("paciente.test@test.cl")).thenReturn(false);
        when(roleRepository.findByName("PATIENT")).thenReturn(Optional.of(patientRole));
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(org.mockito.ArgumentMatchers.any(UserEntity.class))).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(userResponse);

        // ACT: ejecutar método o endpoint.
        UserResponseDTO resultado = service.register(request);

        // ASSERT: verificar resultado esperado.
        assertThat(resultado.getId()).isEqualTo(2L);
        assertThat(resultado.getUsername()).isEqualTo("paciente.test");
        assertThat(resultado.getEmail()).isEqualTo("paciente.test@test.cl");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(userRepository).existsByUsername("paciente.test");
        verify(userRepository).existsByEmail("paciente.test@test.cl");
        verify(roleRepository).findByName("PATIENT");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(org.mockito.ArgumentMatchers.any(UserEntity.class));
        verify(userMapper).toResponse(savedUser);

        // Un ejemplo de falla posible.
        // Se esperaba: usuario registrado con rol PATIENT.
        // Se obtuvo: usuario registrado sin rol.
        // QA deberia reportarlo asi: El metodo register() crea el usuario, pero no le asigna el rol PATIENT.
        // Desarrollo deberia revisar: AuthServiceImpl.register(), AuthServiceImpl.registerUser() y RoleRepository.findByName().
    }

    @Test
    void registerUser_debeRegistrarUsuarioConRolIndicadoYRetornarUsuarioMapeado() {
        // ARRANGE: preparar datos y mocks.
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("doctor.test");
        request.setEmail("doctor.test@test.cl");
        request.setPassword("password123");

        RoleEntity doctorRole = RoleEntity.builder()
                .id(3L)
                .name("DOCTOR")
                .description("Doctor")
                .build();

        UserEntity savedUser = UserEntity.builder()
                .id(3L)
                .username("doctor.test")
                .email("doctor.test@test.cl")
                .password("encoded-password")
                .enabled(true)
                .roles(Set.of(doctorRole))
                .build();

        UserResponseDTO userResponse = UserResponseDTO.builder()
                .id(3L)
                .username("doctor.test")
                .email("doctor.test@test.cl")
                .build();

        when(userRepository.existsByUsername("doctor.test")).thenReturn(false);
        when(userRepository.existsByEmail("doctor.test@test.cl")).thenReturn(false);
        when(roleRepository.findByName("DOCTOR")).thenReturn(Optional.of(doctorRole));
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(org.mockito.ArgumentMatchers.any(UserEntity.class))).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(userResponse);

        // ACT: ejecutar método o endpoint.
        UserResponseDTO resultado = service.registerUser(request, "DOCTOR");

        // ASSERT: verificar resultado esperado.
        assertThat(resultado.getId()).isEqualTo(3L);
        assertThat(resultado.getUsername()).isEqualTo("doctor.test");
        assertThat(resultado.getEmail()).isEqualTo("doctor.test@test.cl");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(userRepository).existsByUsername("doctor.test");
        verify(userRepository).existsByEmail("doctor.test@test.cl");
        verify(roleRepository).findByName("DOCTOR");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(org.mockito.ArgumentMatchers.any(UserEntity.class));
        verify(userMapper).toResponse(savedUser);

        // Un ejemplo de falla posible.
        // Se esperaba: usuario registrado con rol DOCTOR.
        // Se obtuvo: usuario registrado con otro rol.
        // QA deberia reportarlo asi: El metodo registerUser() no respeta el rol indicado al crear el usuario.
        // Desarrollo deberia revisar: AuthServiceImpl.registerUser(), RoleRepository.findByName() y la asignacion de roles.
    }

    @Test
    void getProfile_debeRetornarPerfilDelUsuario() {
        // ARRANGE: preparar datos y mocks.
        RoleEntity adminRole = RoleEntity.builder()
                .id(1L)
                .name("ADMIN")
                .description("Administrador")
                .build();

        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("admin")
                .email("admin@test.cl")
                .password("encoded-password")
                .enabled(true)
                .roles(Set.of(adminRole))
                .build();

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        // ACT: ejecutar método o endpoint.
        UserProfileDTO resultado = service.getProfile("admin");

        // ASSERT: verificar resultado esperado.
        assertThat(resultado.getUsername()).isEqualTo("admin");
        assertThat(resultado.getEmail()).isEqualTo("admin@test.cl");
        assertThat(resultado.getRoles()).containsExactly("ADMIN");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(userRepository).findByUsername("admin");

        // Un ejemplo de falla posible.
        // Se esperaba: perfil del usuario admin.
        // Se obtuvo: perfil de otro usuario.
        // QA deberia reportarlo asi: El metodo getProfile("admin") retorna datos de un usuario distinto al autenticado.
        // Desarrollo deberia revisar: AuthServiceImpl.getProfile() y UserRepository.findByUsername().
    }

    @Test
    void changePassword_debeActualizarPasswordDelUsuario() {
        // ARRANGE: preparar datos y mocks.
        ChangePasswordDTO request = new ChangePasswordDTO();
        request.setCurrentPassword("password123");
        request.setNewPassword("newPassword123");

        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("admin")
                .email("admin@test.cl")
                .password("encoded-old-password")
                .enabled(true)
                .roles(Set.of())
                .build();

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded-old-password")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("encoded-new-password");
        when(userRepository.save(user)).thenReturn(user);

        // ACT: ejecutar método o endpoint.
        service.changePassword("admin", request);

        // ASSERT: verificar resultado esperado.
        assertThat(user.getPassword()).isEqualTo("encoded-new-password");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(userRepository).findByUsername("admin");
        verify(passwordEncoder).matches("password123", "encoded-old-password");
        verify(passwordEncoder).encode("newPassword123");
        verify(userRepository).save(user);

        // Un ejemplo de falla posible.
        // Se esperaba: password actualizado con la nueva contraseña codificada.
        // Se obtuvo: password antiguo sin cambios.
        // QA deberia reportarlo asi: El metodo changePassword() responde correctamente, pero no actualiza la contraseña del usuario.
        // Desarrollo deberia revisar: AuthServiceImpl.changePassword(), PasswordEncoder.encode() y UserRepository.save().
    }

    @Test
    void login_debeLanzarUnauthorizedCuandoUsuarioNoExiste() {
        // ARRANGE: preparar datos y mocks.
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("no.existe");
        request.setPassword("password123");

        when(userRepository.findByUsername("no.existe")).thenReturn(Optional.empty());

        // ACT y ASSERT: ejecutar método y verificar excepción esperada.
        assertThatThrownBy(() -> service.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Credenciales inválidas");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(userRepository).findByUsername("no.existe");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).generateToken(any(UserEntity.class));

        // Un ejemplo de falla posible.
        // Se esperaba: error de credenciales invalidas.
        // Se obtuvo: login exitoso para usuario inexistente.
        // QA deberia reportarlo asi: El metodo login() permite acceso con un usuario que no existe.
        // Desarrollo deberia revisar: AuthServiceImpl.login() y UserRepository.findByUsername().
    }

    @Test
    void login_debeLanzarUnauthorizedCuandoPasswordEsIncorrecta() {
        // ARRANGE: preparar datos y mocks.
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("admin");
        request.setPassword("password-incorrecta");

        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("admin")
                .email("admin@test.cl")
                .password("encoded-password")
                .enabled(true)
                .roles(Set.of())
                .build();

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password-incorrecta", "encoded-password")).thenReturn(false);

        // ACT y ASSERT: ejecutar método y verificar excepción esperada.
        assertThatThrownBy(() -> service.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Credenciales inválidas");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(userRepository).findByUsername("admin");
        verify(passwordEncoder).matches("password-incorrecta", "encoded-password");
        verify(jwtService, never()).generateToken(any(UserEntity.class));

        // Un ejemplo de falla posible.
        // Se esperaba: error de credenciales invalidas.
        // Se obtuvo: token JWT generado con password incorrecta.
        // QA deberia reportarlo asi: El metodo login() permite acceso con contraseña incorrecta.
        // Desarrollo deberia revisar: AuthServiceImpl.login() y PasswordEncoder.matches().
    }

    @Test
    void registerUser_debeLanzarBusinessExceptionCuandoUsernameExiste() {
        // ARRANGE: preparar datos y mocks.
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("doctor.test");
        request.setEmail("doctor.test@test.cl");
        request.setPassword("password123");

        when(userRepository.existsByUsername("doctor.test")).thenReturn(true);

        // ACT y ASSERT: ejecutar método y verificar excepción esperada.
        assertThatThrownBy(() -> service.registerUser(request, "DOCTOR"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("El username ya existe");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(userRepository).existsByUsername("doctor.test");
        verify(userRepository, never()).existsByEmail(any());
        verify(roleRepository, never()).findByName(any());
        verify(userRepository, never()).save(any(UserEntity.class));

        // Un ejemplo de falla posible.
        // Se esperaba: rechazo por username duplicado.
        // Se obtuvo: usuario creado con username repetido.
        // QA deberia reportarlo asi: El metodo registerUser() permite registrar usernames duplicados.
        // Desarrollo deberia revisar: AuthServiceImpl.registerUser() y UserRepository.existsByUsername().
    }

    @Test
    void registerUser_debeLanzarBusinessExceptionCuandoEmailExiste() {
        // ARRANGE: preparar datos y mocks.
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("doctor.test");
        request.setEmail("doctor.test@test.cl");
        request.setPassword("password123");

        when(userRepository.existsByUsername("doctor.test")).thenReturn(false);
        when(userRepository.existsByEmail("doctor.test@test.cl")).thenReturn(true);

        // ACT y ASSERT: ejecutar método y verificar excepción esperada.
        assertThatThrownBy(() -> service.registerUser(request, "DOCTOR"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("El email ya existe");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(userRepository).existsByUsername("doctor.test");
        verify(userRepository).existsByEmail("doctor.test@test.cl");
        verify(roleRepository, never()).findByName(any());
        verify(userRepository, never()).save(any(UserEntity.class));

        // Un ejemplo de falla posible.
        // Se esperaba: rechazo por email duplicado.
        // Se obtuvo: usuario creado con email repetido.
        // QA deberia reportarlo asi: El metodo registerUser() permite registrar emails duplicados.
        // Desarrollo deberia revisar: AuthServiceImpl.registerUser() y UserRepository.existsByEmail().
    }

    @Test
    void registerUser_debeLanzarResourceNotFoundCuandoRolNoExiste() {
        // ARRANGE: preparar datos y mocks.
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("doctor.test");
        request.setEmail("doctor.test@test.cl");
        request.setPassword("password123");

        when(userRepository.existsByUsername("doctor.test")).thenReturn(false);
        when(userRepository.existsByEmail("doctor.test@test.cl")).thenReturn(false);
        when(roleRepository.findByName("DOCTOR")).thenReturn(Optional.empty());

        // ACT y ASSERT: ejecutar método y verificar excepción esperada.
        assertThatThrownBy(() -> service.registerUser(request, "DOCTOR"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Rol DOCTOR no encontrado");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(userRepository).existsByUsername("doctor.test");
        verify(userRepository).existsByEmail("doctor.test@test.cl");
        verify(roleRepository).findByName("DOCTOR");
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any(UserEntity.class));

        // Un ejemplo de falla posible.
        // Se esperaba: rechazo porque el rol DOCTOR no existe.
        // Se obtuvo: usuario creado sin rol valido.
        // QA deberia reportarlo asi: El metodo registerUser() registra usuarios con roles inexistentes.
        // Desarrollo deberia revisar: AuthServiceImpl.registerUser() y RoleRepository.findByName().
    }

    @Test
    void getProfile_debeLanzarResourceNotFoundCuandoUsuarioNoExiste() {
        // ARRANGE: preparar datos y mocks.
        when(userRepository.findByUsername("no.existe")).thenReturn(Optional.empty());

        // ACT y ASSERT: ejecutar método y verificar excepción esperada.
        assertThatThrownBy(() -> service.getProfile("no.existe"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuario no encontrado");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(userRepository).findByUsername("no.existe");

        // Un ejemplo de falla posible.
        // Se esperaba: error porque el usuario no existe.
        // Se obtuvo: perfil retornado con datos vacios o incorrectos.
        // QA deberia reportarlo asi: El metodo getProfile() retorna perfil para un usuario inexistente.
        // Desarrollo deberia revisar: AuthServiceImpl.getProfile() y UserRepository.findByUsername().
    }

    @Test
    void changePassword_debeLanzarResourceNotFoundCuandoUsuarioNoExiste() {
        // ARRANGE: preparar datos y mocks.
        ChangePasswordDTO request = new ChangePasswordDTO();
        request.setCurrentPassword("password123");
        request.setNewPassword("newPassword123");

        when(userRepository.findByUsername("no.existe")).thenReturn(Optional.empty());

        // ACT y ASSERT: ejecutar método y verificar excepción esperada.
        assertThatThrownBy(() -> service.changePassword("no.existe", request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuario no encontrado");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(userRepository).findByUsername("no.existe");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(userRepository, never()).save(any(UserEntity.class));

        // Un ejemplo de falla posible.
        // Se esperaba: error porque el usuario no existe.
        // Se obtuvo: intento de actualizar contraseña para usuario inexistente.
        // QA deberia reportarlo asi: El metodo changePassword() intenta cambiar password de un usuario que no existe.
        // Desarrollo deberia revisar: AuthServiceImpl.changePassword() y UserRepository.findByUsername().
    }

    @Test
    void changePassword_debeLanzarUnauthorizedCuandoPasswordActualEsIncorrecta() {
        // ARRANGE: preparar datos y mocks.
        ChangePasswordDTO request = new ChangePasswordDTO();
        request.setCurrentPassword("password-incorrecta");
        request.setNewPassword("newPassword123");

        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("admin")
                .email("admin@test.cl")
                .password("encoded-old-password")
                .enabled(true)
                .roles(Set.of())
                .build();

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password-incorrecta", "encoded-old-password")).thenReturn(false);

        // ACT y ASSERT: ejecutar método y verificar excepción esperada.
        assertThatThrownBy(() -> service.changePassword("admin", request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("La contraseña actual es incorrecta");

        // VERIFY: comprobar llamadas al mock si corresponde.
        verify(userRepository).findByUsername("admin");
        verify(passwordEncoder).matches("password-incorrecta", "encoded-old-password");
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any(UserEntity.class));

        // Un ejemplo de falla posible.
        // Se esperaba: rechazo por contraseña actual incorrecta.
        // Se obtuvo: contraseña actualizada aunque la password actual no coincide.
        // QA deberia reportarlo asi: El metodo changePassword() permite cambiar contraseña sin validar correctamente la actual.
        // Desarrollo deberia revisar: AuthServiceImpl.changePassword() y PasswordEncoder.matches().
    }
}
