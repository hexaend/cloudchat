package ru.hexaend.auth_service.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.hexaend.auth_service.entity.Role;
import ru.hexaend.auth_service.entity.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User createEnabledUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash("hash");
        user.setFirstName("Alex");
        user.setLastName("Ivanov");
        user.setEnabled(true);
        user.setEmailVerified(false);
        return userRepository.save(user);
    }

    @DisplayName("findByUsername returns enabled user with roles")
    @Test
    void findByUsernameReturnsEnabledUserWithRoles() {
        User saved = createEnabledUser("alexey", "alexey@test.io");
        Role role = new Role();
        role.setName("ADMIN");
        roleRepository.save(role);
        saved.getRoles().add(role);
        userRepository.save(saved);

        Optional<User> result = userRepository.findByUsername("alexey");

        assertThat(result).isPresent();
        assertThat(result.get().getRoles()).hasSize(1);
        assertThat(result.get().isEnabled()).isTrue();
    }

    @DisplayName("existsByUsernameAndEnabledIsTrue respects enabled flag")
    @Test
    void existsByUsernameOnlyCountsEnabledUsers() {
        User enabled = createEnabledUser("enabled", "enabled@test.io");
        User disabled = createEnabledUser("disabled", "disabled@test.io");
        disabled.setEnabled(false);
        userRepository.save(disabled);

        assertThat(userRepository.existsByUsernameAndEnabledIsTrue("enabled")).isTrue();
        assertThat(userRepository.existsByUsernameAndEnabledIsTrue("disabled")).isFalse();
    }

    @DisplayName("existsByEmailAndEnabledIsTrue respects enabled flag")
    @Test
    void existsByEmailOnlyCountsEnabledUsers() {
        User user = createEnabledUser("test", "test@test.io");
        user.setEnabled(false);
        userRepository.save(user);

        assertThat(userRepository.existsByEmailAndEnabledIsTrue("test@test.io")).isFalse();

        user.setEnabled(true);
        userRepository.save(user);

        assertThat(userRepository.existsByEmailAndEnabledIsTrue("test@test.io")).isTrue();
    }

    @DisplayName("getByEmail returns enabled user")
    @Test
    void getByEmailReturnsEnabledUser() {
        createEnabledUser("alexey", "alex@domain.io");

        Optional<User> result = userRepository.getByEmail("alex@domain.io");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("alex@domain.io");
    }
}

