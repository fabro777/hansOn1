package com.tuempresa.userauthservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * DTO para mostrar información de usuarios sin datos sensibles.
 */
@Schema(description = "Información pública del usuario")
public class UserInfoDto {

    @Schema(description = "ID único del usuario", example = "1")
    private Long id;

    @Schema(description = "Nombre de usuario", example = "usuario123")
    private String username;

    @Schema(description = "Email del usuario", example = "usuario@example.com")
    private String email;

    @Schema(description = "Fecha de creación de la cuenta", example = "2025-09-30T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Estado del usuario (activo/inactivo)", example = "true")
    private Boolean isActive;

    public UserInfoDto() {}

    public UserInfoDto(Long id, String username, String email, LocalDateTime createdAt, Boolean isActive) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
        this.isActive = isActive;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
