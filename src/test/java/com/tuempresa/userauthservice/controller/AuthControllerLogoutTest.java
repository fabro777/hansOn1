package com.tuempresa.userauthservice.controller;

import com.tuempresa.userauthservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerLogoutTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void logout_tokenValido_ok() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token_de_autenticacion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Logout exitoso"));
    }

    @Test
    void logout_tokenInvalido_unauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token_invalido"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Token inválido"));
    }

    @Test
    void logout_sinToken_badRequest() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Token requerido"));
    }
}
