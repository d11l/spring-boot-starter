package sa.abdulrahman.starter.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static sa.abdulrahman.starter.validation.Regex.PHONE_NUMBER;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Pattern(regexp = PHONE_NUMBER, message = "Phone number must be 9-15 digits and may start with a '+' sign")
    private String phone;
}
