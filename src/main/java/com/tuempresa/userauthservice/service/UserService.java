package com.tuempresa.userauthservice.service;

import com.tuempresa.userauthservice.dto.UserLoginDto;
import com.tuempresa.userauthservice.dto.UserRegistrationDto;
import com.tuempresa.userauthservice.model.User;
import com.tuempresa.userauthservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de usuarios y autenticación.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registra un nuevo usuario en el sistema.
     * @param registrationDto datos del usuario a registrar
     * @return usuario registrado
     * @throws RuntimeException si el usuario o email ya existen
     */
    public User registerUser(UserRegistrationDto registrationDto) {
        // Verificar si el usuario ya existe
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        // Verificar si el email ya existe
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crear nuevo usuario
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setEmail(registrationDto.getEmail());

        return userRepository.save(user);
    }

    /**
     * Autentica un usuario con sus credenciales.
     * @param loginDto credenciales del usuario
     * @return usuario autenticado
     * @throws RuntimeException si las credenciales son inválidas
     */
    public User authenticateUser(UserLoginDto loginDto) {
        Optional<User> userOpt = userRepository.findByUsername(loginDto.getUsername());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        if (!user.getIsActive()) {
            throw new RuntimeException("Usuario inactivo");
        }

        return user;
    }

    /**
     * Busca un usuario por nombre de usuario.
     * @param username nombre de usuario
     * @return Optional con el usuario si existe
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Obtiene la lista de todos los usuarios registrados.
     * @return lista de usuarios
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Desactiva un usuario (logout lógico).
     * @param username nombre de usuario
     * @return true si se desactivó correctamente
     */
    public boolean logoutUser(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsActive(false);
            userRepository.save(user);
            return true;
        }

        return false;
    }
}
