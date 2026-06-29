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

        RoleEntity adminRole = createRoleIfNotExists(
                "ADMIN",
                "Administrador del sistema"
        );

        RoleEntity patientRole = createRoleIfNotExists(
                "PATIENT",
                "Paciente de la clínica"
        );

        RoleEntity doctorRole = createRoleIfNotExists(
                "DOCTOR",
                "Médico de la clínica"
        );

        createUserIfNotExists(
                "admin",
                "admin@clinicademo.cl",
                "admin123",
                adminRole
        );

        createUserIfNotExists(
                "paciente.camila",
                "camila.torres@clinicademo.cl",
                "paciente123",
                patientRole
        );

        createUserIfNotExists(
                "paciente.mario",
                "mario.reyes@clinicademo.cl",
                "paciente123",
                patientRole
        );

        createUserIfNotExists(
                "paciente.valentina",
                "valentina.soto@clinicademo.cl",
                "paciente123",
                patientRole
        );

        createUserIfNotExists(
                "medico.andrea",
                "andrea.morales@clinicademo.cl",
                "medico123",
                doctorRole
        );

        createUserIfNotExists(
                "medico.rodrigo",
                "rodrigo.fuentes@clinicademo.cl",
                "medico123",
                doctorRole
        );

        createUserIfNotExists(
                "medico.paula",
                "paula.herrera@clinicademo.cl",
                "medico123",
                doctorRole
        );

        log.info("Datos iniciales verificados correctamente");
    }

    private RoleEntity createRoleIfNotExists(String roleName, String description) {

        return roleRepository.findByName(roleName)
                .orElseGet(() -> {

                    RoleEntity role = RoleEntity.builder()
                            .name(roleName)
                            .description(description)
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

        boolean usernameExists = userRepository.existsByUsername(username);
        boolean emailExists = userRepository.existsByEmail(email);

        if (usernameExists) {

            log.info("Usuario ya existe: {}", username);

            return;
        }

        if (emailExists) {

            log.warn(
                    "Inconsistencia de datos semilla: el email {} ya existe pero el username {} no existe. No se creará el usuario.",
                    email,
                    username
            );

            return;
        }

        UserEntity user = UserEntity.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .roles(Set.of(role))
                .build();

        userRepository.save(user);

        log.info("Usuario creado: {}", username);
    }
}