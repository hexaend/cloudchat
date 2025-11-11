package ru.hexaend.auth_service.service.interfaces;

import org.springframework.transaction.annotation.Transactional;
import ru.hexaend.auth_service.dto.request.RegisterRequest;
import ru.hexaend.auth_service.dto.response.VerifyStatusRequest;
import ru.hexaend.auth_service.entity.User;

public interface UserDetailsService extends org.springframework.security.core.userdetails.UserDetailsService {

 VerifyStatusRequest register(RegisterRequest request);

 @Transactional
 VerifyStatusRequest verifyEmail(User user);

 User getCurrentUser();

 User getUser(String username);
}
