package com.tuempresa.userauthservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuempresa.userauthservice.dto.UserRegistrationDto;
import com.tuempresa.userauthservice.model.User;
import com.tuempresa.userauthservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    private UserRegistrationDto registrationDto;
    private User user;

    @BeforeEach
    void setUp() {
        registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("nuevoUsuario");
        registrationDto.setPassword("passwordValida");
        registrationDto.setEmail("nuevo@correo.com");

        user = new User();
        user.setId(1L);
        user.setUsername("nuevoUsuario");
        user.setPassword("hashedPassword");
        user.setEmail("nuevo@correo.com");
    }

    @Test
    void register_successfulRegistration_returnsRegistroExitoso() throws Exception {
        Mockito.when(userService.registerUser(any(UserRegistrationDto.class))).thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Registro exitoso"))
                .andExpect(jsonPath("$.data").value("nuevoUsuario"));
    }
}

