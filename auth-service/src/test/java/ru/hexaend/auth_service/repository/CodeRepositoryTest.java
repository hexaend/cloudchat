package ru.hexaend.auth_service.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.hexaend.auth_service.entity.Code;
import ru.hexaend.auth_service.entity.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CodeRepositoryTest {

    @Autowired
    private CodeRepository codeRepository;

    @Autowired
    private UserRepository userRepository;

    private User createUser() {
        User user = new User();
        user.setUsername("alexey");
        user.setEmail("alexey@mail.io");
        user.setPasswordHash("hash");
        user.setFirstName("Alex");
        user.setLastName("Ivanov");
        return userRepository.save(user);
    }

    @DisplayName("findByCodeAndType returns code when matches")
    @Test
    void findByCodeAndTypeReturnsMatch() {
        User user = createUser();
        Code code = new Code();
        code.setCode("123456");
        code.setType(Code.VerificationCodeType.EMAIL_VERIFICATION);
        code.setUser(user);
        codeRepository.save(code);

        Optional<Code> result = codeRepository.findByCodeAndType("123456", Code.VerificationCodeType.EMAIL_VERIFICATION);

        assertThat(result).isPresent();
        assertThat(result.get().getUser()).isEqualTo(user);
    }
}
