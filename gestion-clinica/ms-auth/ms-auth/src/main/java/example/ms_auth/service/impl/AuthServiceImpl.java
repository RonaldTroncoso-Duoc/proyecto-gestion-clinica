package example.ms_auth.service.impl;

import example.ms_auth.dto.request.ChangePasswordDTO;
import example.ms_auth.dto.request.LoginRequestDTO;
import example.ms_auth.dto.request.RegisterRequestDTO;
import example.ms_auth.dto.response.AuthResponseDTO;
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
import example.ms_auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import example.ms_auth.dto.response.UserProfileDTO;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final UserMapper userMapper;

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {

        log.info("Intentando login para usuario: {}", request.getUsername());

        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new UnauthorizedException("Credenciales inválidas")
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {

            log.warn("Password incorrecta para usuario {}", request.getUsername());

            throw new UnauthorizedException("Credenciales inválidas");
        }

        String token = jwtService.generateToken(user);

        log.info("Login exitoso para usuario {}", request.getUsername());

        return AuthResponseDTO.builder()
                .token(token)
                .username(user.getUsername())
                .roles(
                        user.getRoles()
                                .stream()
                                .map(RoleEntity::getName)
                                .toList()
                )
                .build();
    }

    @Override
    public UserResponseDTO register(RegisterRequestDTO request) {
        log.info("Registrando usuario {} con rol PATIENT", request.getUsername());
        return registerUser(request, "PATIENT");
    }

    @Override
    public UserResponseDTO registerUser(RegisterRequestDTO request, String roleName) {

        log.info("Registrando usuario {} con rol {}", request.getUsername(), roleName);

        if (userRepository.existsByUsername(request.getUsername())) {

            log.error("Username ya existe: {}", request.getUsername());

            throw new BusinessException("El username ya existe");
        }

        if (userRepository.existsByEmail(request.getEmail())) {

            log.error("Email ya registrado: {}", request.getEmail());

            throw new BusinessException("El email ya existe");
        }

        RoleEntity role = roleRepository.findByName(roleName)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Rol " + roleName + " no encontrado")
                );

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .roles(Set.of(role))
                .build();

        UserEntity savedUser = userRepository.save(user);

        log.info("Usuario registrado correctamente con rol {}: {}", roleName, savedUser.getUsername());

        return userMapper.toResponse(savedUser);
    }

        @Override
        @Transactional(readOnly = true)
        public UserProfileDTO getProfile(String username) {

        log.info("Obteniendo perfil de usuario {}", username);

        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Usuario no encontrado")
            );

    return UserProfileDTO.builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .roles(
                    user.getRoles()
                            .stream()
                            .map(RoleEntity::getName)
                            .toList()
            )
            .build();
}

@Override
public void changePassword(
        String username,
        ChangePasswordDTO request
) {

    log.info("Cambiando password para usuario {}", username);

    UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Usuario no encontrado")
            );

    if (!passwordEncoder.matches(
            request.getCurrentPassword(),
            user.getPassword()
    )) {

        throw new UnauthorizedException(
                "La contraseña actual es incorrecta"
        );
    }

    user.setPassword(
            passwordEncoder.encode(request.getNewPassword())
    );

    userRepository.save(user);

    log.info("Password actualizada correctamente para {}", username);
}
}