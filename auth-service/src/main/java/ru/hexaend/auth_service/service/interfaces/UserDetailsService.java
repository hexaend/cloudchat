package ru.hexaend.auth_service.service.interfaces;

import org.springframework.transaction.annotation.Transactional;
import ru.hexaend.auth_service.dto.request.ChangePasswordRequest;
import ru.hexaend.auth_service.dto.request.RegisterRequest;
import ru.hexaend.auth_service.dto.request.ResetPasswordRequest;
import ru.hexaend.auth_service.dto.response.ResetPasswordResponse;
import ru.hexaend.auth_service.dto.response.VerifyStatusResponse;
import ru.hexaend.auth_service.entity.Role;
import ru.hexaend.auth_service.entity.User;

public interface UserDetailsService extends org.springframework.security.core.userdetails.UserDetailsService {

    VerifyStatusResponse register(RegisterRequest request);

    VerifyStatusResponse verifyEmail(User user);

    User getCurrentUser();

    User getUserByUsername(String username);

    void setEmailVerified(User user);

    ResetPasswordResponse resetPassword(ResetPasswordRequest request); // TODO: change from void to response

    void changePassword(ChangePasswordRequest request);

    void logoutAllSessions(User user);

    Role getDefaultUserRole();

    User getUserByIdOrUsername(Long userId, String username);
}
