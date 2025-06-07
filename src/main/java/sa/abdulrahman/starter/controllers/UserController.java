package sa.abdulrahman.starter.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sa.abdulrahman.starter.dto.user.ResetPasswordDTO;
import sa.abdulrahman.starter.dto.user.UserUpdateDTO;
import sa.abdulrahman.starter.records.Profile;
import sa.abdulrahman.starter.services.UserService;

import static sa.abdulrahman.starter.constants.URLs.USER.*;
import static sa.abdulrahman.starter.utils.SecurityUtils.getCurrentUserId;

@RestController
@RequestMapping(BASE_URL)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(RESET_PASSWORD)
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordDTO resetPasswordDTO) {
        userService.resetPassword(getCurrentUserId(), resetPasswordDTO);
        return ResponseEntity.ok("Password reset successful.");
    }

    @PostMapping(UPDATE_PROFILE)
    public ResponseEntity<?> updateProfile(@RequestBody @Valid UserUpdateDTO updateDTO) {
        Profile updatedProfile = userService.updateUserProfile(getCurrentUserId(), updateDTO);
        return ResponseEntity.ok(updatedProfile);
    }

}
