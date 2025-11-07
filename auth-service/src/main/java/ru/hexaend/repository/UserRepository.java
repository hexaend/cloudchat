package ru.hexaend.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hexaend.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.enabled = true  ")
    @EntityGraph(attributePaths = { "roles", "roles.authorities" })
    Optional<User> findByUsername(String username);

    Boolean existsByUsernameAndEnabledIsTrue(String username);
    Boolean existsByEmailAndEnabledIsTrue(String email);
}
