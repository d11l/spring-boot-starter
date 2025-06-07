package sa.abdulrahman.starter.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sa.abdulrahman.starter.constants.Roles;
import sa.abdulrahman.starter.dto.auth.*;
import sa.abdulrahman.starter.dto.user.ResetPasswordDTO;
import sa.abdulrahman.starter.dto.user.UserUpdateDTO;
import sa.abdulrahman.starter.exceptions.*;
import sa.abdulrahman.starter.models.CustomUserDetails;
import sa.abdulrahman.starter.models.Role;
import sa.abdulrahman.starter.models.User;
import sa.abdulrahman.starter.records.Profile;
import sa.abdulrahman.starter.repositories.RoleRepository;
import sa.abdulrahman.starter.repositories.UserDetailsRepository;
import sa.abdulrahman.starter.repositories.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    protected CustomUserDetails create(RegisterRequest request){
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .build();

        user = userRepository.save(user);

        // Find or create the user role
        Role userRole = roleRepository.findByName(Roles.USER)
                .orElseGet(() -> {
                    Role newRole = Role.builder()
                            .name(Roles.USER)
                            .build();
                    return roleRepository.save(newRole);
                });

        List<Role> roles = new ArrayList<>();
        roles.add(userRole);

        CustomUserDetails userDetails = CustomUserDetails.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .user(user)
                .enabled(true)
                .accountNonLocked(true)
                .build();

        return userDetailsRepository.save(userDetails);
    }

    public void resetPassword(Long userId, ResetPasswordDTO resetPasswordDTO) {
        log.info("Password reset request for user ID: {}", userId);

        // Get the user details
        CustomUserDetails userDetails = userDetailsRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found during password reset: {}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        // Verify the current password
        if (!passwordEncoder.matches(resetPasswordDTO.getCurrentPassword(), userDetails.getPassword())) {
            log.warn("Password reset failed: Incorrect current password for user ID: {}", userId);
            throw new InvalidPasswordException("Current password is incorrect");
        }

        // Update password
        userDetails.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        userDetailsRepository.save(userDetails);

        log.info("Password reset successful for user ID: {}", userId);
    }

    public Profile updateUserProfile(Long userId, UserUpdateDTO updateDTO) {
        log.info("Profile update request for user ID: {}", userId);

        // Get the user and user details
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found during profile update: {}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        CustomUserDetails userDetails = userDetailsRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User details not found during profile update: {}", userId);
                    return new ResourceNotFoundException("User details not found");
                });

        // Update user profile information
        if (updateDTO.getFirstName() != null) {
            log.debug("Updating first name for user ID: {}", userId);
            user.setFirstName(updateDTO.getFirstName());
        }

        if (updateDTO.getLastName() != null) {
            log.debug("Updating last name for user ID: {}", userId);
            user.setLastName(updateDTO.getLastName());
        }

        if (updateDTO.getPhone() != null) {
            log.debug("Updating phone for user ID: {}", userId);
            user.setPhone(updateDTO.getPhone());
        }

        // Save updated user
        user = userRepository.save(user);
        log.info("Profile updated successfully for user ID: {}", userId);

        // Return updated profile
        return new Profile(
            user.getFirstName(),
            user.getLastName(),
            userDetails.getUsername(),
            user.getPhone()
        );
    }

}
