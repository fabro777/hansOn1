package com.tuempresa.userauthservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para las respuestas de la API.
 */
@Schema(description = "Formato estándar de respuesta de la API")
public class ApiResponseDto {

    @Schema(description = "Indica si la operación fue exitosa", example = "true")
    private boolean success;

    @Schema(description = "Mensaje descriptivo del resultado", example = "Usuario registrado exitosamente")
    private String message;

    @Schema(description = "Datos adicionales de la respuesta", example = "usuario123")
    private Object data;

    public ApiResponseDto() {}

    public ApiResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ApiResponseDto(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Métodos estáticos para facilitar la creación de respuestas
    public static ApiResponseDto success(String message) {
        return new ApiResponseDto(true, message);
    }

    public static ApiResponseDto success(String message, Object data) {
        return new ApiResponseDto(true, message, data);
    }

    public static ApiResponseDto error(String message) {
        return new ApiResponseDto(false, message);
    }

    // Getters y Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
