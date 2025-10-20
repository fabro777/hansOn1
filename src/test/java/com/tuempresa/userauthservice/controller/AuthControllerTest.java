package com.tuempresa.userauthservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuempresa.userauthservice.dto.UserLoginDto;
import com.tuempresa.userauthservice.dto.UserRegistrationDto;
import com.tuempresa.userauthservice.model.User;
import com.tuempresa.userauthservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    // TC001 - Registro exitoso
    @Test
    void register_datosValidos_retorna200ConMensajeExito() throws Exception {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("usuario123");
        registrationDto.setPassword("contraseña123");
        registrationDto.setEmail("usuario@email.com");

        User user = new User();
        user.setUsername("usuario123");
        user.setEmail("usuario@email.com");

        when(userService.registerUser(any(UserRegistrationDto.class))).thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Registro exitoso"));

        verify(userService).registerUser(any(UserRegistrationDto.class));
    }

    // TC002 - Registro con nombre de usuario existente
    @Test
    void register_usuarioExistente_retorna409ConError() throws Exception {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("usuarioExistente");
        registrationDto.setPassword("contraseña123");
        registrationDto.setEmail("usuario@email.com");

        when(userService.registerUser(any(UserRegistrationDto.class)))
                .thenThrow(new RuntimeException("Nombre de usuario ya registrado"));

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Nombre de usuario ya registrado"));
    }

    // TC003 - Registro con datos inválidos
    @Test
    void register_datosInvalidos_retorna400ConError() throws Exception {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername(""); // nombre vacío
        registrationDto.setPassword("123"); // contraseña corta
        registrationDto.setEmail("email-invalido");

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // TC004 - Login exitoso
    @Test
    void login_credencialesCorrectas_retorna200ConToken() throws Exception {
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername("usuario123");
        loginDto.setPassword("contraseña123");

        User user = new User();
        user.setUsername("usuario123");
        user.setIsActive(true);

        when(userService.authenticateUser(any(UserLoginDto.class))).thenReturn(user);

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login exitoso"));

        verify(userService).authenticateUser(any(UserLoginDto.class));
    }

    // TC005 - Login con contraseña incorrecta
    @Test
    void login_contraseñaIncorrecta_retorna401ConError() throws Exception {
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername("usuario123");
        loginDto.setPassword("contraseñaIncorrecta");

        when(userService.authenticateUser(any(UserLoginDto.class)))
                .thenThrow(new RuntimeException("Credenciales inválidas"));

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
    }

    // TC006 - Login con usuario inexistente
    @Test
    void login_usuarioInexistente_retorna404ConError() throws Exception {
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername("usuarioInexistente");
        loginDto.setPassword("contraseña123");

        when(userService.authenticateUser(any(UserLoginDto.class)))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Usuario no encontrado"));
    }

    // TC007 - Logout exitoso
    @Test
    void logout_tokenValido_retorna200ConMensajeExito() throws Exception {
        doNothing().when(userService).logoutUser("token_de_autenticacion");

        mockMvc.perform(post("/api/auth/logout")
                .with(csrf())
                .header("Authorization", "Bearer token_de_autenticacion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Logout exitoso"));

        verify(userService).logoutUser("token_de_autenticacion");
    }

    // TC008 - Logout con token inválido
    @Test
    void logout_tokenInvalido_retorna401ConError() throws Exception {
        doThrow(new RuntimeException("Token inválido"))
                .when(userService).logoutUser("token_invalido");

        mockMvc.perform(post("/api/auth/logout")
                .with(csrf())
                .header("Authorization", "Bearer token_invalido"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Token inválido"));
    }

    // TC009 - Logout sin token
    @Test
    void logout_sinToken_retorna400ConError() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Token requerido"));
    }

    // Test adicional para listar usuarios
    @Test
    void getUsers_usuariosExistentes_retorna200ConLista() throws Exception {
        User user1 = new User();
        user1.setUsername("usuario1");
        User user2 = new User();
        user2.setUsername("usuario2");

        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/api/auth/users")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void getUsers_sinUsuarios_retorna200ConArregloVacio() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/auth/users")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }
}
