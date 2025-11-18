package ru.hexaend.auth_service.rest;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.hexaend.auth_service.security.JwtAuthFilter;

@AutoConfigureMockMvc(addFilters = false)
public abstract class BaseWebMvcTest {
    @MockitoBean
    protected JwtAuthFilter jwtRequestFilter;
}