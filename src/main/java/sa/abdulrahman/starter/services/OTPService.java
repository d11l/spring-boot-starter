package sa.abdulrahman.starter.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sa.abdulrahman.starter.exceptions.InvalidOTPException;
import sa.abdulrahman.starter.models.OTP;
import sa.abdulrahman.starter.repositories.OTPRepository;
import sa.abdulrahman.starter.services.integrations.EmailService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OTPService {

    private final OTPRepository otpRepository;
    private final EmailService emailService;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;

    public void generateAndSendOTP(String username) {
        // Check if there's a recent OTP that's not expired
        Optional<OTP> recentOtp = otpRepository.findTopByUsernameOrderByExpiryDateDesc(username);
        if (recentOtp.isPresent() && !recentOtp.get().isNotValid()) {
            log.info("Recent valid OTP found for username: {}, reusing it", username);
            sendOTPViaEmail(username, recentOtp.get().getCode());
            return;
        }

        log.info("Generating new OTP for username: {}", username);
        String otpCode = generateOTP();

        // Save the OTP
        OTP otp = OTP.builder()
                .username(username)
                .code(otpCode)
                .expiryDate(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .used(false)
                .build();

        otpRepository.save(otp);
        sendOTPViaEmail(username, otpCode);
    }

    public void validateOTP(String username, String otpCode) {
        log.info("Validating OTP for username: {}", username);

        Optional<OTP> otpExistsOptional = otpRepository.findTopByUsernameOrderByExpiryDateDesc(username);

        if (otpExistsOptional.isPresent()) {
            OTP existingOtp = otpExistsOptional.get();
            existingOtp.setAttempts(existingOtp.getAttempts() + 1);


            if (existingOtp.hasExceededMaxAttempts()) {
                log.warn("OTP validation failed: Max attempts exceeded for username: {}", username);
                otpRepository.save(existingOtp);
                throw new InvalidOTPException();
            }

            if (existingOtp.isExpired()) {
                log.warn("OTP validation failed: OTP expired for username: {}", username);
                otpRepository.save(existingOtp);
                throw new InvalidOTPException();
            }

            if (existingOtp.isUsed()) {
                log.warn("OTP validation failed: OTP already used for username: {}", username);
                otpRepository.save(existingOtp);
                throw new InvalidOTPException();
            }

            if(existingOtp.getCode().equals(otpCode)) {
                log.info("OTP validated successfully for username: {}", username);
                existingOtp.setUsed(true);
                otpRepository.save(existingOtp);
                return;
            }

            otpRepository.save(existingOtp);
        }
        log.warn("OTP validation failed for username: {}", username);

        throw new InvalidOTPException();
    }

    private String generateOTP() {
        return String.format("%0"+OTP_LENGTH+"d", new Random().nextInt(1000000));
    }

    private void sendOTPViaEmail(String email, String otpCode) {
        String subject = "Your One-Time Password";
        String body = String.format("Your OTP is: %s. It will expire in %d minutes.", otpCode, OTP_EXPIRY_MINUTES);
        emailService.send(email, subject, body);
    }

}
