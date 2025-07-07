package sa.abdulrahman.starter.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import sa.abdulrahman.starter.dto.auth.*;
import sa.abdulrahman.starter.exceptions.*;
import sa.abdulrahman.starter.models.CustomUserDetails;
import sa.abdulrahman.starter.models.Role;
import sa.abdulrahman.starter.models.User;
import sa.abdulrahman.starter.records.AuthResponse;
import sa.abdulrahman.starter.records.OTPResponse;
import sa.abdulrahman.starter.repositories.UserDetailsRepository;
import sa.abdulrahman.starter.security.jwt.Jwt;
import sa.abdulrahman.starter.services.integrations.EmailService;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserService userService;
    private final UserDetailsRepository userDetailsRepository;
    private final AuthenticationManager authenticationManager;
    private final OTPService otpService;
    private final Jwt jwt;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private final Integer LOCK_HOURS = 3;
    private final Integer MAX_FAILED_LOGIN_ATTEMPTS = 3;

    public AuthResponse register(RegisterRequest request) {
        log.info("Processing registration request for username: {}", request.getUsername());

        if (userDetailsRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration failed: Username already taken: {}", request.getUsername());
            throw new UsernameAlreadyTakenException();
        }

        otpService.validateOTP(request.getUsername(), request.getOtp());

        CustomUserDetails userDetails = userService.create(request);

        log.info("User registered successfully: {}", request.getUsername());
        return createAuthResponse(userDetails);
    }

    public AuthResponse login(AuthRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());

        // Check if an account is locked but lock time has passed (3 hours)
        userDetailsRepository.findByUsername(request.getUsername()).ifPresent(userDetails -> {
            if (!userDetails.isAccountNonLocked() && userDetails.getLockTime() != null) {
                if (LocalDateTime.now().isAfter(userDetails.getLockTime().plusHours(LOCK_HOURS))) {
                    // Unlock the account if 3 hours have passed
                    userDetails.setAccountNonLocked(true);
                    userDetails.setFailedLoginAttempts(0);
                    userDetails.setLockTime(null);
                    userDetailsRepository.save(userDetails);
                    log.info("Account unlocked for username: {} after 3 hours", request.getUsername());
                }
            }
        });

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

            log.info("Login successful for username: {}", request.getUsername());
            return createAuthResponse(user);
        }
        catch (Exception e) {
            handleFailedLogin(request.getUsername());
            throw new UnauthorizedException();
        }
    }

    public OTPResponse requestOTP(OTPRequest request) {
        String username = request.getUsername();
        log.info("OTP request for username: {}", username);
        otpService.generateAndSendOTP(request.getUsername());
        boolean userExists = userDetailsRepository.existsByUsername(username);
        log.info("OTP generated and sent for username: {}", request.getUsername());
        return new OTPResponse("OTP Sent Successfully", userExists ? "login" : "register");
    }

    public AuthResponse loginWithOTP(@Valid @RequestBody UsernameAndOTP request) {
        log.info("Login with OTP attempt for username: {}", request.getUsername());

        otpService.validateOTP(request.getUsername(), request.getOtp());
        CustomUserDetails user = userDetailsRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("User not found during OTP login: {}", request.getUsername());
                    return new UsernameNotFoundException("User not found");
                });

        log.info("Login with OTP successful for username: {}", request.getUsername());
        return createAuthResponse(user);
    }

    public AuthResponse refreshToken(String refreshToken) {
        log.info("Token refresh request received");

        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            log.warn("Invalid refresh token format");
            throw new InvalidTokenException("Invalid refresh token");
        }

        String token = refreshToken.substring(7);
        String username = jwt.extractUsername(token);

        if (username == null) {
            log.warn("Could not extract username from refresh token");
            throw new InvalidTokenException("Invalid refresh token");
        }

        CustomUserDetails user = userDetailsRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found during token refresh: {}", username);
                    return new UsernameNotFoundException("User not found");
                });

        if (!jwt.validateToken(token, user)) {
            log.warn("Token validation failed for username: {}", username);
            throw new InvalidTokenException("Invalid refresh token");
        }

        log.info("Token refreshed successfully for username: {}", username);
        return createAuthResponse(user);
    }

    private AuthResponse createAuthResponse(CustomUserDetails userDetails) {
        String token = jwt.generateToken(userDetails);
        String refreshToken = jwt.generateRefreshToken(userDetails);

        User user = userDetails.getUser();
        String[] roleNames = userDetails.getRoles().stream()
                .map(Role::getName)
                .toArray(String[]::new);

        return new AuthResponse(token, refreshToken, userDetails.getUsername(), user.getFirstName(), user.getLastName(), roleNames);
    }

    public void forgotPassword(OTPRequest request) {
        log.info("Forgot password request for username: {}", request.getUsername());
        if (userDetailsRepository.existsByUsername(request.getUsername())) {
            otpService.generateAndSendOTP(request.getUsername());
        } else {
            log.info("Forgot password request for non-existent user: {}", request.getUsername());
        }
    }


    public void resetPasswordWithOTP(ResetPasswordWithOTPRequest request) {
        log.info("Reset password with OTP request for username: {}", request.getUsername());

        otpService.validateOTP(request.getUsername(), request.getOtp());
        CustomUserDetails userDetails = userDetailsRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("User not found during password reset with OTP: {}", request.getUsername());
                    return new ResourceNotFoundException("User not found");
                });

        userDetails.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userDetailsRepository.save(userDetails);
        log.info("Password reset with OTP successful for user: {}", request.getUsername());
    }

    private void handleFailedLogin(String username) {
        log.info("Handling failed login attempt for username: {}", username);
        // Find the user
        userDetailsRepository.findByUsername(username).ifPresent(userDetails -> {

            // If an account is not locked, increment failed attempts
            if (userDetails.isAccountNonLocked()) {
                userDetails.setFailedLoginAttempts(userDetails.getFailedLoginAttempts() + 1);

                // Lock account after 3 failed attempts
                if (userDetails.getFailedLoginAttempts() >= MAX_FAILED_LOGIN_ATTEMPTS) {
                    userDetails.setAccountNonLocked(false);
                    userDetails.setLockTime(LocalDateTime.now());
                    log.warn("Account locked for username: {} after 3 failed attempts", username);

                    // Send email notification
                    emailService.send(username,
                            "Account Locked - Security Alert",
                            "Your account has been locked due to 3 failed login attempts. " +
                                    "It will be automatically unlocked after 3 hours. " +
                                    "If you did not attempt to log in, please reset your password immediately."
                    );
                    log.info("Account lock notification email sent to: {}", username);
                }


                userDetailsRepository.save(userDetails);
            }
            // Check if an account is already locked and lock time has passed (3 hours)
            else if (userDetails.getLockTime() != null){
                if (LocalDateTime.now().isAfter(userDetails.getLockTime().plusHours(LOCK_HOURS))) {
                    // Unlock the account if 3 hours have passed
                    userDetails.setAccountNonLocked(true);
                    userDetails.setFailedLoginAttempts(0);
                    userDetails.setLockTime(null);
                    userDetailsRepository.save(userDetails);
                    log.info("Account unlocked for username: {} after 3 hours", username);
                }
            }
        });
    }

}
