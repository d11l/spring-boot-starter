package sa.abdulrahman.starter.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sa.abdulrahman.starter.dto.auth.*;
import sa.abdulrahman.starter.records.AuthResponse;
import sa.abdulrahman.starter.services.UserService;

import static sa.abdulrahman.starter.constants.URLs.AUTH.*;

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;;

    @PostMapping(LOGIN)
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping(REQUEST_OTP)
    public ResponseEntity<String> requestOTP(@Valid @RequestBody OTPRequest request) {
        userService.requestOTP(request);
        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping(LOGIN_OTP)
    public ResponseEntity<AuthResponse> loginWithOTP(@Valid @RequestBody UsernameAndOTP request) {
        return ResponseEntity.ok(userService.loginWithOTP(request));
    }

    @PostMapping(REGISTER)
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping(REFRESH_TOKEN)
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        return ResponseEntity.ok(userService.refreshToken(refreshToken));
    }

    @PostMapping(FORGOT_PASSWORD)
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody OTPRequest request) {
        userService.forgotPassword(request);
        return ResponseEntity.ok("If your email is registered, you will receive a password reset OTP.");
    }

    @PostMapping(RESET_PASSWORD_WITH_OTP)
    public ResponseEntity<String> resetPasswordWithOTP(@Valid @RequestBody ResetPasswordWithOTPRequest request) {
        userService.resetPasswordWithOTP(request);
        return ResponseEntity.ok("Password has been reset successfully.");
    }
}
