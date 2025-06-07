package sa.abdulrahman.starter.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static sa.abdulrahman.starter.validation.Regex.EMAIL;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OTPRequest {

    @NotBlank(message = "Email is required")
    @Pattern(regexp = EMAIL, message = "Invalid email format")
    private String username;
}
