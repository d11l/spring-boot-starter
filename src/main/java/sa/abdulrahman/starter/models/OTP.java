package sa.abdulrahman.starter.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "otps")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OTP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private boolean used = false;

    @Column(nullable = false)
    private int attempts = 0;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public boolean hasExceededMaxAttempts() {
        return attempts > 3;
    }

    public boolean isNotValid() {
        return  used || this.hasExceededMaxAttempts() || this.isExpired();
    }
}
