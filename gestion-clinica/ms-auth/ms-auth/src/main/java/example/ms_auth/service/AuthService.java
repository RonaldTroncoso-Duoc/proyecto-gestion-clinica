package example.ms_auth.service;

import example.ms_auth.dto.request.ChangePasswordDTO;
import example.ms_auth.dto.request.LoginRequestDTO;
import example.ms_auth.dto.request.RegisterRequestDTO;
import example.ms_auth.dto.response.AuthResponseDTO;
import example.ms_auth.dto.response.UserProfileDTO;
import example.ms_auth.dto.response.UserResponseDTO;


public interface AuthService {

    AuthResponseDTO login(LoginRequestDTO request);

    UserResponseDTO register(RegisterRequestDTO request);

    UserProfileDTO getProfile(String username);

    void changePassword(String username, ChangePasswordDTO request);
}