package ru.hexaend.auth_service.service;

import ru.hexaend.auth_service.dto.request.RegisterRequest;
import ru.hexaend.auth_service.dto.response.RegisterStatusResponse;

public interface UserDetailsService extends org.springframework.security.core.userdetails.UserDetailsService {

 RegisterStatusResponse register(RegisterRequest request);

}
