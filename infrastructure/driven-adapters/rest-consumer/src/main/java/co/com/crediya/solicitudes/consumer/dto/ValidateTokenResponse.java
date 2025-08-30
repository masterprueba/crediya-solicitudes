package co.com.crediya.solicitudes.consumer.dto;

public record ValidateTokenResponse(
    String userId,
    String email,
    String role
) {}
