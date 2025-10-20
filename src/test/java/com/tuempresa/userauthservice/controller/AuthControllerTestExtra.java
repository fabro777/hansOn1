package com.tuempresa.userauthservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuempresa.userauthservice.dto.UserRegistrationDto;
import com.tuempresa.userauthservice.dto.UserLoginDto;
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
class AuthControllerTestExtra {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    private UserRegistrationDto registrationDto;
    private UserLoginDto loginDto;
    private User user;

    @BeforeEach
    void setUp() {
        registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("nuevoUsuario");
        registrationDto.setPassword("passwordValida");
        registrationDto.setEmail("nuevo@correo.com");

        loginDto = new UserLoginDto();
        loginDto.setUsername("nuevoUsuario");
        loginDto.setPassword("passwordValida");

        user = new User();
        user.setId(1L);
        user.setUsername("nuevoUsuario");
        user.setPassword("hashedPassword");
        user.setEmail("nuevo@correo.com");
    }

    @Test
    void register_usuarioExistente_conflict() throws Exception {
        Mockito.when(userService.registerUser(any(UserRegistrationDto.class)))
                .thenThrow(new RuntimeException("El nombre de usuario ya está en uso"));
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Nombre de usuario ya registrado"));
    }

    @Test
    void register_datosInvalidos_badRequest() throws Exception {
        UserRegistrationDto invalidDto = new UserRegistrationDto();
        invalidDto.setUsername("");
        invalidDto.setPassword("123");
        invalidDto.setEmail("correo");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_exitoso_ok() throws Exception {
        Mockito.when(userService.authenticateUser(any(UserLoginDto.class))).thenReturn(user);
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login exitoso"));
    }

    @Test
    void login_contrasenaIncorrecta_unauthorized() throws Exception {
        Mockito.when(userService.authenticateUser(any(UserLoginDto.class)))
                .thenThrow(new RuntimeException("Credenciales inválidas"));
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
    }

    @Test
    void login_usuarioNoExistente_notFound() throws Exception {
        Mockito.when(userService.authenticateUser(any(UserLoginDto.class)))
                .thenThrow(new RuntimeException("Usuario no encontrado"));
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Usuario no encontrado"));
    }
}
package com.tuempresa.userauthservice.service;

import com.tuempresa.userauthservice.dto.UserRegistrationDto;
import com.tuempresa.userauthservice.dto.UserLoginDto;
import com.tuempresa.userauthservice.model.User;
import com.tuempresa.userauthservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class UserServiceTest {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        userService = new UserService();
        userService.userRepository = userRepository;
        userService.passwordEncoder = passwordEncoder;
    }

    @Test
    void registerUser_usuarioNoRegistrado_registroExitoso() {
        UserRegistrationDto dto = new UserRegistrationDto("nuevoUsuario", "passwordValida", "nuevo@correo.com");
        Mockito.when(userRepository.existsByUsername(dto.getUsername())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(dto.getPassword())).thenReturn("hashedPassword");
        User user = new User("nuevoUsuario", "hashedPassword", "nuevo@correo.com");
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);
        User result = userService.registerUser(dto);
        assertEquals("nuevoUsuario", result.getUsername());
    }

    @Test
    void registerUser_usuarioExistente_conflict() {
        UserRegistrationDto dto = new UserRegistrationDto("existente", "passwordValida", "nuevo@correo.com");
        Mockito.when(userRepository.existsByUsername(dto.getUsername())).thenReturn(true);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.registerUser(dto));
        assertTrue(ex.getMessage().contains("usuario"));
    }

    @Test
    void registerUser_datosInvalidos_badRequest() {
        UserRegistrationDto dto = new UserRegistrationDto("", "123", "correo");
        // La validación de DTO se hace en el controlador, aquí solo se prueba el flujo normal
        Mockito.when(userRepository.existsByUsername(dto.getUsername())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(dto.getPassword())).thenReturn("hashedPassword");
        User user = new User("", "hashedPassword", "correo");
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);
        User result = userService.registerUser(dto);
        assertEquals("", result.getUsername());
    }

    @Test
    void authenticateUser_loginExitoso() {
        UserLoginDto dto = new UserLoginDto("usuario", "passwordValida");
        User user = new User("usuario", "hashedPassword", "correo@correo.com");
        Mockito.when(userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(true);
        User result = userService.authenticateUser(dto);
        assertEquals("usuario", result.getUsername());
    }

    @Test
    void authenticateUser_contrasenaIncorrecta_unauthorized() {
        UserLoginDto dto = new UserLoginDto("usuario", "passwordIncorrecta");
        User user = new User("usuario", "hashedPassword", "correo@correo.com");
        Mockito.when(userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(false);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.authenticateUser(dto));
        assertTrue(ex.getMessage().contains("inválidas"));
    }

    @Test
    void authenticateUser_usuarioNoExistente_notFound() {
        UserLoginDto dto = new UserLoginDto("noexiste", "password");
        Mockito.when(userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.authenticateUser(dto));
        assertTrue(ex.getMessage().contains("no encontrado"));
    }
}

