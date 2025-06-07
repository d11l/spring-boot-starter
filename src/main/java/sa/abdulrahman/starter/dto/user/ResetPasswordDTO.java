package sa.abdulrahman.starter.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static sa.abdulrahman.starter.validation.Regex.PASSWORD;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDTO {

    @NotBlank(message = "Current password is required")
    @Pattern(regexp = PASSWORD, message = "Password must be at least 8 characters and can only contain letters, numbers, and special characters (@, _, #, $)")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Pattern(regexp = PASSWORD, message = "Password must be at least 8 characters and can only contain letters, numbers, and special characters (@, _, #, $)")
    private String newPassword;
}
