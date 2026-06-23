package example.ms_medicos.client;

import example.ms_medicos.dto.AuthRegisterRequestDTO;
import example.ms_medicos.dto.AuthUserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-auth")
public interface AuthClient {

    @PostMapping("/api/auth/internal/register")
    AuthUserResponseDTO registerUser(@RequestBody AuthRegisterRequestDTO request);
}