package example.ms_auth.config;

import example.ms_auth.entity.RoleEntity;
import example.ms_auth.entity.UserEntity;
import example.ms_auth.repository.RoleRepository;
import example.ms_auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        RoleEntity adminRole = createRoleIfNotExists("ADMIN");

        RoleEntity patientRole = createRoleIfNotExists("PATIENT");

        RoleEntity doctorRole = createRoleIfNotExists("DOCTOR");

        createUserIfNotExists(
                "admin",
                "admin@gmail.com",
                "admin123",
                adminRole
        );

        createUserIfNotExists(
                "doctor",
                "doctor@gmail.com",
                "doctor123",
                doctorRole
        );

        log.info("Datos iniciales verificados correctamente");
    }

    private RoleEntity createRoleIfNotExists(String roleName) {

        return roleRepository.findByName(roleName)
                .orElseGet(() -> {

                    RoleEntity role = RoleEntity.builder()
                            .name(roleName)
                            .build();

                    log.info("Rol creado: {}", roleName);

                    return roleRepository.save(role);
                });
    }

    private void createUserIfNotExists(
            String username,
            String email,
            String password,
            RoleEntity role
    ) {

        boolean exists = userRepository.existsByUsername(username);

        if (!exists) {

            UserEntity user = UserEntity.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .enabled(true)
                    .roles(Set.of(role))
                    .build();

            userRepository.save(user);

            log.info("Usuario creado: {}", username);

        } else {

            log.info("Usuario ya existe: {}", username);
        }
    }
}