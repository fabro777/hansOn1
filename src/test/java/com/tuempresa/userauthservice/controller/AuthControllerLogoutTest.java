package com.tuempresa.userauthservice.controller;

import com.tuempresa.userauthservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerLogoutTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        Mockito.when(userService.getUsernameFromToken(anyString())).thenReturn("usuario123");
    }

    @Test
    void logout_tokenValido_ok() throws Exception {
        Mockito.when(userService.isTokenValid("token_de_autenticacion")).thenReturn(true);
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer token_de_autenticacion")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Logout exitoso"));
    }

    @Test
    void logout_tokenInvalido_unauthorized() throws Exception {
        Mockito.when(userService.isTokenValid("token_invalido")).thenReturn(false);
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer token_invalido")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Token inválido"));
    }

    @Test
    void logout_sinToken_badRequest() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Token requerido"));
    }
}
package com.tuempresa.userauthservice.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceLogoutTest {
    private final UserService userService = new UserService();

    @Test
    void isTokenValid_tokenValido_true() {
        assertTrue(userService.isTokenValid("token_de_autenticacion"));
    }

    @Test
    void isTokenValid_tokenInvalido_false() {
        assertFalse(userService.isTokenValid("token_invalido"));
    }

    @Test
    void getUsernameFromToken_tokenValido_usuario() {
        assertEquals("usuario123", userService.getUsernameFromToken("token_de_autenticacion"));
    }
}

