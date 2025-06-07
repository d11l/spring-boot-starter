package sa.abdulrahman.starter.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sa.abdulrahman.starter.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {}