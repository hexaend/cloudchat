package ru.hexaend.auth_service.service.interfaces;

import ru.hexaend.auth_service.entity.RefreshToken;

public interface CodeService {
    void saveRefreshToken(RefreshToken refreshToken);
}
