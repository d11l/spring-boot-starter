package sa.abdulrahman.starter.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sa.abdulrahman.starter.models.CustomUserDetails;

import java.util.Optional;

@Repository
public interface UserDetailsRepository extends JpaRepository<CustomUserDetails, Long> {
    Optional<CustomUserDetails> findByUsername(String username);
    boolean existsByUsername(String username);
}