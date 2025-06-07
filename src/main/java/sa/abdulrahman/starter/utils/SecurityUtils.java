package sa.abdulrahman.starter.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import sa.abdulrahman.starter.models.CustomUserDetails;

@Component
public class SecurityUtils {
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
             if (authentication.getPrincipal() instanceof CustomUserDetails) {
                return ((CustomUserDetails) authentication.getPrincipal()).getId();
            }
        }
        throw new IllegalStateException("User ID could not be determined");
    }

    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            return authentication.getAuthorities()
                    .stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role));
        }
        return false;
    }
}
