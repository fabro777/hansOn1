package com.tuempresa.userauthservice.service;

import com.tuempresa.userauthservice.dto.UserLoginDto;
import com.tuempresa.userauthservice.dto.UserRegistrationDto;
import com.tuempresa.userauthservice.model.User;
import com.tuempresa.userauthservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // TC001 - Registro exitoso
    @Test
    void registerUser_datosValidos_registroExitoso() {
        // Arrange
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("usuario123");
        registrationDto.setPassword("contraseña123");
        registrationDto.setEmail("usuario@email.com");

        when(userRepository.existsByUsername("usuario123")).thenReturn(false);
        when(userRepository.existsByEmail("usuario@email.com")).thenReturn(false);
        when(passwordEncoder.encode("contraseña123")).thenReturn("contraseña_encriptada");

        User savedUser = new User();
        savedUser.setUsername("usuario123");
        savedUser.setEmail("usuario@email.com");
        savedUser.setPassword("contraseña_encriptada");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.registerUser(registrationDto);

        // Assert
        assertNotNull(result);
        assertEquals("usuario123", result.getUsername());
        assertEquals("usuario@email.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    // TC002 - Registro con nombre de usuario existente
    @Test
    void registerUser_usuarioExistente_lanzaExcepcion() {
        // Arrange
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("usuarioExistente");
        registrationDto.setPassword("contraseña123");
        registrationDto.setEmail("usuario@email.com");

        when(userRepository.existsByUsername("usuarioExistente")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.registerUser(registrationDto));
        assertEquals("El nombre de usuario ya está en uso", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    // TC003 - Registro con datos inválidos (se valida en el controlador, pero test de cobertura)
    @Test
    void registerUser_emailExistente_lanzaExcepcion() {
        // Arrange
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("nuevoUsuario");
        registrationDto.setPassword("contraseña123");
        registrationDto.setEmail("email@existente.com");

        when(userRepository.existsByUsername("nuevoUsuario")).thenReturn(false);
        when(userRepository.existsByEmail("email@existente.com")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.registerUser(registrationDto));
        assertEquals("El email ya está registrado", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    // TC004 - Login exitoso
    @Test
    void authenticateUser_credencialesCorrectas_loginExitoso() {
        // Arrange
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername("usuario123");
        loginDto.setPassword("contraseña123");

        User user = new User();
        user.setUsername("usuario123");
        user.setPassword("contraseña_encriptada");
        user.setIsActive(true);

        when(userRepository.findByUsername("usuario123")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("contraseña123", "contraseña_encriptada")).thenReturn(true);

        // Act
        User result = userService.authenticateUser(loginDto);

        // Assert
        assertNotNull(result);
        assertEquals("usuario123", result.getUsername());
        assertTrue(result.getIsActive());
    }

    // TC005 - Login con contraseña incorrecta
    @Test
    void authenticateUser_contraseñaIncorrecta_lanzaExcepcion() {
        // Arrange
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername("usuario123");
        loginDto.setPassword("contraseñaIncorrecta");

        User user = new User();
        user.setUsername("usuario123");
        user.setPassword("contraseña_encriptada");
        user.setIsActive(true);

        when(userRepository.findByUsername("usuario123")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("contraseñaIncorrecta", "contraseña_encriptada")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.authenticateUser(loginDto));
        assertEquals("Credenciales inválidas", exception.getMessage());
    }

    // TC006 - Login con usuario inexistente
    @Test
    void authenticateUser_usuarioInexistente_lanzaExcepcion() {
        // Arrange
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername("usuarioInexistente");
        loginDto.setPassword("contraseña123");

        when(userRepository.findByUsername("usuarioInexistente")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.authenticateUser(loginDto));
        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    // TC007 - Logout exitoso
    @Test
    void logoutUser_tokenValido_desactivaUsuario() {
        // Arrange
        String token = "token_de_autenticacion";
        User user = new User();
        user.setUsername("usuario123");
        user.setIsActive(true);

        when(userRepository.findByUsername("usuario123")).thenReturn(Optional.of(user));

        // Act
        userService.logoutUser(token);

        // Assert
        assertFalse(user.getIsActive());
        verify(userRepository).save(user);
    }

    // TC008 - Logout con token inválido
    @Test
    void logoutUser_tokenInvalido_lanzaExcepcion() {
        // Arrange
        String token = "token_invalido";

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.logoutUser(token));
        assertEquals("Token inválido", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    // TC009 - Logout sin token
    @Test
    void logoutUser_tokenVacio_lanzaExcepcion() {
        // Arrange
        String token = "";

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.logoutUser(token));
        assertEquals("Token inválido", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void logoutUser_tokenNull_lanzaExcepcion() {
        // Arrange
        String token = null;

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.logoutUser(token));
        assertEquals("Token inválido", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticateUser_usuarioInactivo_lanzaExcepcion() {
        // Arrange
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername("usuario123");
        loginDto.setPassword("contraseña123");

        User user = new User();
        user.setUsername("usuario123");
        user.setPassword("contraseña_encriptada");
        user.setIsActive(false);

        when(userRepository.findByUsername("usuario123")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("contraseña123", "contraseña_encriptada")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.authenticateUser(loginDto));
        assertEquals("Usuario inactivo", exception.getMessage());
    }
}
