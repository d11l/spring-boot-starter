package sa.abdulrahman.starter.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static sa.abdulrahman.starter.validation.Regex.EMAIL;
import static sa.abdulrahman.starter.validation.Regex.OTP;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsernameAndOTP {

    @NotBlank(message = "Email is required")
    @Pattern(regexp = EMAIL, message = "Invalid email format")
    private String username;

    @NotBlank(message = "OTP is required")
    @Pattern(regexp = OTP, message = "OTP must be 6 digits")
    private String otp;
}
