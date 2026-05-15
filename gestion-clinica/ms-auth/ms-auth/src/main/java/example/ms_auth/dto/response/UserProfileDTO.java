package example.ms_auth.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserProfileDTO {

    private String username;

    private String email;

    private List<String> roles;
}