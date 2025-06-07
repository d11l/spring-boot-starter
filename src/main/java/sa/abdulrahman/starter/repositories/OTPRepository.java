package sa.abdulrahman.starter.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sa.abdulrahman.starter.models.OTP;

import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {
    Optional<OTP> findTopByUsernameOrderByExpiryDateDesc(String username);
}
