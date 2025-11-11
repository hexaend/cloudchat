package ru.hexaend.auth_service.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.hexaend.auth_service.dto.request.RegisterRequest;
import ru.hexaend.auth_service.dto.response.UserResponse;
import ru.hexaend.auth_service.entity.User;
import ru.hexaend.auth_service.exception.EmailAlreadyInUseException;
import ru.hexaend.auth_service.exception.UsernameAlreadyInUseException;
import ru.hexaend.auth_service.mapper.UserMapper;
import ru.hexaend.auth_service.repository.UserRepository;
import ru.hexaend.auth_service.service.UserDetailsService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private final String username = "alexey";
    private final String password = "securePassword!";
    private final String email = "alexey@example.com";

    @DisplayName("loadUserByUsername returns user when found and enabled")
    @Test
    void loadUserByUsernameReturnsUserWhenFoundAndEnabled() {
        // given
        User user = mock(User.class);

        // when
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // then
        User result = userDetailsService.loadUserByUsername(username);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository).findByUsername(username);
    }

    @DisplayName("loadUserByUsername throws when user not found")
    @Test
    void loadUserByUsernameThrowsWhenNotFound() {
        // when
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // then
        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });

        verify(userRepository).findByUsername(username);
    }

    @DisplayName("register creates user when username and email are free")
    @Test
    void registerCreatesUserWhenFree() {
        // given
        RegisterRequest request = new RegisterRequest(username, password, email, "Alex", "Ivanov");
        User mappedUser = new User();

        // when
        when(userRepository.existsByEmailAndEnabledIsTrue(email)).thenReturn(false);
        when(userRepository.existsByUsernameAndEnabledIsTrue(username)).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(mappedUser);
        when(passwordEncoder.encode(password)).thenReturn("encoded");
        when(userMapper.toDto(mappedUser)).thenReturn(new UserResponse(1L, username, email, "Alex", "Ivanov", false));

        // then
        UserResponse response = userDetailsService.register(request);

        assertNotNull(response);
        assertEquals(username, response.username());
        assertEquals(email, response.email());

        verify(userRepository).existsByEmailAndEnabledIsTrue(email);
        verify(userRepository).existsByUsernameAndEnabledIsTrue(username);
        verify(userMapper).toEntity(request);
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(mappedUser);
        verify(userMapper).toDto(mappedUser);
    }

    @DisplayName("register throws when email already in use")
    @Test
    void registerThrowsWhenEmailInUse() {
        // given
        RegisterRequest request = new RegisterRequest(username, password, email, "Alex", "Ivanov");

        // when
        when(userRepository.existsByEmailAndEnabledIsTrue(email)).thenReturn(true);

        // then
        EmailAlreadyInUseException ex = assertThrows(EmailAlreadyInUseException.class, () -> userDetailsService.register(request));
        assertEquals("Email is already in use", ex.getMessage());

        verify(userRepository).existsByEmailAndEnabledIsTrue(email);
        verify(userRepository, never()).save(any());
    }

    @DisplayName("register throws when username already in use")
    @Test
    void registerThrowsWhenUsernameInUse() {
        // given
        RegisterRequest request = new RegisterRequest(username, password, email, "Alex", "Ivanov");

        // when
        when(userRepository.existsByEmailAndEnabledIsTrue(email)).thenReturn(false);
        when(userRepository.existsByUsernameAndEnabledIsTrue(username)).thenReturn(true);

        // then
        UsernameAlreadyInUseException ex = assertThrows(UsernameAlreadyInUseException.class, () -> userDetailsService.register(request));
        assertEquals("Username is already in use", ex.getMessage());

        verify(userRepository).existsByEmailAndEnabledIsTrue(email);
        verify(userRepository).existsByUsernameAndEnabledIsTrue(username);
        verify(userRepository, never()).save(any());
    }
}
