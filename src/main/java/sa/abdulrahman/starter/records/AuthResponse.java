package sa.abdulrahman.starter.records;

public record AuthResponse(
        String token,
        String refreshToken,
        String username,
        String firstName,
        String lastName,
        String[] roles
        ) {}