package ru.hexaend.auth_service.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.hexaend.auth_service.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.enabled = true  ")
    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findByUsername(@Param("username") String username);

    @Query(
            """
                   SELECT exists(SELECT u FROM User u
                    WHERE u.username = :username AND u.enabled = true)"""
    )
    Boolean existsByUsernameAndEnabledIsTrue(@Param("username") String username);

    @Query("""
            SELECT EXISTS(SELECT u FROM User u
             WHERE u.email = :email AND u.enabled = true)
            """)
    Boolean existsByEmailAndEnabledIsTrue(@Param("email") String email);

    @Query("""
            SELECT u FROM User u
            WHERE u.email = :email AND u.enabled = true
            """)
    Optional<User> getByEmail(@Param("email") String email);
}
