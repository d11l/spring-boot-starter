package sa.abdulrahman.starter.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sa.abdulrahman.starter.dto.auth.*;
import sa.abdulrahman.starter.records.AuthResponse;
import sa.abdulrahman.starter.records.OTPResponse;
import sa.abdulrahman.starter.services.AuthService;

import static sa.abdulrahman.starter.constants.URLs.AUTH.*;

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;;

    @PostMapping(LOGIN)
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(REQUEST_OTP)
    public ResponseEntity<OTPResponse> requestOTP(@Valid @RequestBody OTPRequest request) {
        return ResponseEntity.ok(authService.requestOTP(request));
    }

    @PostMapping(LOGIN_OTP)
    public ResponseEntity<AuthResponse> loginWithOTP(@Valid @RequestBody UsernameAndOTP request) {
        return ResponseEntity.ok(authService.loginWithOTP(request));
    }

    @PostMapping(REGISTER)
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping(REFRESH_TOKEN)
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping(FORGOT_PASSWORD)
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody OTPRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok("If your email is registered, you will receive a password reset OTP.");
    }

    @PostMapping(RESET_PASSWORD_WITH_OTP)
    public ResponseEntity<String> resetPasswordWithOTP(@Valid @RequestBody ResetPasswordWithOTPRequest request) {
        authService.resetPasswordWithOTP(request);
        return ResponseEntity.ok("Password has been reset successfully.");
    }
}
