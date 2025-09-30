package com.tuempresa.userauthservice.controller;

import com.tuempresa.userauthservice.dto.ApiResponseDto;
import com.tuempresa.userauthservice.dto.UserLoginDto;
import com.tuempresa.userauthservice.dto.UserRegistrationDto;
import com.tuempresa.userauthservice.dto.UserInfoDto;
import com.tuempresa.userauthservice.model.User;
import com.tuempresa.userauthservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para la autenticación de usuarios.
 * Expone endpoints para login, registro y logout.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints para gestión de autenticación de usuarios")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * Endpoint para registrar un nuevo usuario.
     * @param registrationDto datos del usuario a registrar
     * @return respuesta con el resultado del registro
     */
    @PostMapping("/register")
    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Crea un nuevo usuario en el sistema con username, password y email únicos"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuario registrado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    value = "{\"success\": true, \"message\": \"Usuario registrado exitosamente\", \"data\": \"usuario123\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error en los datos de entrada o usuario ya existe",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    value = "{\"success\": false, \"message\": \"El nombre de usuario ya está en uso\"}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponseDto> register(
        @Parameter(description = "Datos del usuario a registrar", required = true)
        @Valid @RequestBody UserRegistrationDto registrationDto) {
        try {
            User user = userService.registerUser(registrationDto);
            return ResponseEntity.ok(
                ApiResponseDto.success("Usuario registrado exitosamente", user.getUsername())
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                ApiResponseDto.error(e.getMessage())
            );
        }
    }

    /**
     * Endpoint para iniciar sesión.
     * @param loginDto credenciales del usuario
     * @param request petición HTTP para manejo de sesión
     * @return respuesta con el resultado del login
     */
    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica a un usuario y crea una sesión HTTP"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login exitoso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    value = "{\"success\": true, \"message\": \"Login exitoso\", \"data\": \"usuario123\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciales inválidas",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    value = "{\"success\": false, \"message\": \"Credenciales inválidas\"}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponseDto> login(
        @Parameter(description = "Credenciales del usuario", required = true)
        @Valid @RequestBody UserLoginDto loginDto,
        HttpServletRequest request) {
        try {
            User user = userService.authenticateUser(loginDto);

            // Crear sesión
            HttpSession session = request.getSession(true);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("userId", user.getId());

            return ResponseEntity.ok(
                ApiResponseDto.success("Login exitoso", user.getUsername())
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponseDto.error(e.getMessage())
            );
        }
    }

    /**
     * Endpoint para cerrar sesión.
     * @param request petición HTTP para manejo de sesión
     * @return respuesta con el resultado del logout
     */
    @PostMapping("/logout")
    @Operation(
        summary = "Cerrar sesión",
        description = "Invalida la sesión actual del usuario"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Logout exitoso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    value = "{\"success\": true, \"message\": \"Logout exitoso\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "No hay sesión activa",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    value = "{\"success\": false, \"message\": \"No hay sesión activa\"}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponseDto> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            String username = (String) session.getAttribute("username");

            // Invalidar sesión
            session.invalidate();

            if (username != null) {
                // Desactivar usuario (logout lógico)
                userService.logoutUser(username);
                return ResponseEntity.ok(
                    ApiResponseDto.success("Logout exitoso")
                );
            }
        }

        return ResponseEntity.badRequest().body(
            ApiResponseDto.error("No hay sesión activa")
        );
    }

    /**
     * Endpoint para verificar el estado de la sesión.
     * @param request petición HTTP para verificar sesión
     * @return respuesta con el estado de la sesión
     */
    @GetMapping("/status")
    @Operation(
        summary = "Verificar estado de sesión",
        description = "Verifica si existe una sesión activa para el usuario"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Sesión activa",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    value = "{\"success\": true, \"message\": \"Sesión activa\", \"data\": \"usuario123\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No hay sesión activa",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    value = "{\"success\": false, \"message\": \"No hay sesión activa\"}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponseDto> getSessionStatus(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("username") != null) {
            String username = (String) session.getAttribute("username");
            return ResponseEntity.ok(
                ApiResponseDto.success("Sesión activa", username)
            );
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ApiResponseDto.error("No hay sesión activa")
        );
    }

    /**
     * Endpoint para listar todos los usuarios.
     * @return lista de usuarios registrados en el sistema
     */
    @GetMapping("/users")
    @Operation(
        summary = "Listar usuarios",
        description = "Obtiene una lista de todos los usuarios registrados en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de usuarios obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = {
                    @ExampleObject(
                        name = "Con usuarios",
                        value = "{\"success\": true, \"message\": \"Usuarios obtenidos exitosamente\", \"data\": [{\"id\": 1, \"username\": \"usuario1\", \"email\": \"usuario1@ejemplo.com\", \"createdAt\": \"2025-09-30T10:30:00\", \"isActive\": true}]}"
                    ),
                    @ExampleObject(
                        name = "Sin usuarios",
                        value = "{\"success\": true, \"message\": \"No hay usuarios registrados en el sistema\", \"data\": []}"
                    )
                }
            )
        )
    })
    public ResponseEntity<ApiResponseDto> listUsers() {
        List<User> users = userService.getAllUsers();
        List<UserInfoDto> userDtos = users.stream()
            .map(user -> new UserInfoDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getIsActive()
            ))
            .collect(Collectors.toList());

        if (userDtos.isEmpty()) {
            return ResponseEntity.ok(
                ApiResponseDto.success("No hay usuarios registrados en el sistema", userDtos)
            );
        }

        return ResponseEntity.ok(
            ApiResponseDto.success("Usuarios obtenidos exitosamente", userDtos)
        );
    }
}
