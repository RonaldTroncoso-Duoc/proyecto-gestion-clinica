package example.ms_pacientes.client;

import example.ms_pacientes.dto.AuthRegisterRequestDTO;
import example.ms_pacientes.dto.AuthUserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-auth")
public interface AuthClient {

    @PostMapping("/api/auth/internal/register")
    AuthUserResponseDTO registerUser(@RequestBody AuthRegisterRequestDTO request);
}