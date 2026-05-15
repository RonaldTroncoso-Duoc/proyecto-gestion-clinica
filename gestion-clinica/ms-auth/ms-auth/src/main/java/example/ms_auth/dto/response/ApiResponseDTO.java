package example.ms_auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponseDTO<T> {

    private boolean success;

    private String message;

    private T data;

    public static <T> ApiResponseDTO<T> success(String message, T data) {
        return ApiResponseDTO.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponseDTO<T> error(String message) {
        return ApiResponseDTO.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}