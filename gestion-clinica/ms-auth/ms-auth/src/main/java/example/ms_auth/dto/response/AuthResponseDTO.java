package example.ms_auth.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AuthResponseDTO {

    private String token;

    private String username;

    private List<String> roles;
}