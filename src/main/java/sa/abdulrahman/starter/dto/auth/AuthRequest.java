package sa.abdulrahman.starter.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static sa.abdulrahman.starter.validation.Regex.EMAIL;
import static sa.abdulrahman.starter.validation.Regex.PASSWORD;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

    @NotBlank(message = "Email is required")
    @Pattern(regexp = EMAIL, message = "Invalid email format")
    private String username;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = PASSWORD, message = "Password must be at least 8 characters and can only contain letters, numbers, and special characters (@, _, #, $)")
    private String password;
}
