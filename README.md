# User Authentication Service

Servicio simple de autenticación de usuarios desarrollado con Spring Boot, siguiendo los lineamientos organizacionales establecidos.

## Características

- **Framework**: Spring Boot 3.1.5 con Java 17
- **Base de datos**: H2 en memoria (para desarrollo)
- **Seguridad**: Spring Security con BCrypt
- **Endpoints**: Login, Registro y Logout
- **Arquitectura**: Siguiendo patrones Service, DTO, Controller

## Endpoints Disponibles

### 1. Registro de Usuario
```
POST /api/auth/register
Content-Type: application/json

{
    "username": "usuario123",
    "password": "mipassword123",
    "email": "usuario@example.com"
}
```

### 2. Login
```
POST /api/auth/login
Content-Type: application/json

{
    "username": "usuario123",
    "password": "mipassword123"
}
```

### 3. Logout
```
POST /api/auth/logout
```

### 4. Estado de Sesión (adicional)
```
GET /api/auth/status
```

### 5. Listar Usuarios
```
GET /api/auth/users
```

## Estructura del Proyecto

```
src/main/java/com/tuempresa/userauthservice/
├── UserAuthServiceApplication.java    # Clase principal
├── controller/
│   └── AuthController.java           # Controladores REST
├── service/
│   └── UserService.java             # Lógica de negocio
├── dto/
│   ├── UserRegistrationDto.java     # DTO para registro
│   ├── UserLoginDto.java            # DTO para login
│   └── ApiResponseDto.java          # DTO para respuestas
├── model/
│   └── User.java                    # Entidad de usuario
├── repository/
│   └── UserRepository.java          # Acceso a datos
└── config/
    └── SecurityConfig.java          # Configuración de seguridad
```

## Cómo ejecutar

1. Asegúrate de tener Java 17 instalado
2. Ejecuta el proyecto:
   ```bash
   ./mvnw spring-boot:run
   ```
3. La aplicación estará disponible en: `http://localhost:8080`
4. **Documentación Swagger UI**: `http://localhost:8080/swagger-ui.html`
5. **API Docs JSON**: `http://localhost:8080/v3/api-docs`
6. Consola H2 disponible en: `http://localhost:8080/h2-console`

## Documentación OpenAPI/Swagger

### Acceso a Swagger UI
Una vez que la aplicación esté ejecutándose, puedes acceder a la documentación interactiva en:

**🔗 http://localhost:8080/swagger-ui.html**

### Características de Swagger UI:
- **Interfaz visual**: Explora todos los endpoints disponibles
- **Pruebas en vivo**: Ejecuta requests directamente desde la interfaz
- **Documentación completa**: Descripción detallada de cada endpoint
- **Ejemplos**: Datos de ejemplo para cada request y response
- **Validaciones**: Información sobre las validaciones de cada campo

### Endpoints documentados:
- ✅ **POST** `/api/auth/register` - Registro de usuarios
- ✅ **POST** `/api/auth/login` - Inicio de sesión
- ✅ **POST** `/api/auth/logout` - Cierre de sesión
- ✅ **GET** `/api/auth/status` - Estado de la sesión
- ✅ **GET** `/api/auth/users` - Listar usuarios

### API Documentation JSON:
Si necesitas la especificación OpenAPI en formato JSON:
**🔗 http://localhost:8080/v3/api-docs**

## Ejemplos de Uso con cURL (para probar en Insomnia)

### 1. Registro de Usuario
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "usuario123",
    "password": "mipassword123",
    "email": "usuario@example.com"
  }'
```

**Respuesta exitosa:**
```json
{
  "success": true,
  "message": "Usuario registrado exitosamente",
  "data": "usuario123"
}
```

### 2. Login (Inicio de Sesión)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "username": "usuario123",
    "password": "mipassword123"
  }'
```

**Respuesta exitosa:**
```json
{
  "success": true,
  "message": "Login exitoso",
  "data": "usuario123"
}
```

### 3. Verificar Estado de Sesión
```bash
curl -X GET http://localhost:8080/api/auth/status \
  -b cookies.txt
```

**Respuesta con sesión activa:**
```json
{
  "success": true,
  "message": "Sesión activa",
  "data": "usuario123"
}
```

### 4. Logout (Cerrar Sesión)
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -b cookies.txt
```

**Respuesta exitosa:**
```json
{
  "success": true,
  "message": "Logout exitoso"
}
```

### 5. Listar Usuarios
```bash
curl -X GET http://localhost:8080/api/auth/users \
  -b cookies.txt
```

**Respuesta exitosa:**
```json
{
  "success": true,
  "message": "Usuarios obtenidos exitosamente",
  "data": [
    {
      "id": 1,
      "username": "usuario123",
      "email": "usuario@example.com",
      "createdAt": "2025-09-30T10:30:00",
      "isActive": true
    },
    {
      "id": 2,
      "username": "usuario456",
      "email": "usuario456@example.com",
      "createdAt": "2025-09-30T11:00:00",
      "isActive": true
    }
  ]
}
```

**Respuesta cuando no hay usuarios:**
```json
{
  "success": true,
  "message": "No hay usuarios registrados en el sistema",
  "data": []
}
```

## Configuración para Insomnia

### Headers necesarios:
- `Content-Type: application/json`

### Gestión de Sesiones:
Insomnia maneja automáticamente las cookies de sesión, pero también puedes:
1. Habilitar "Send cookies automatically" en la configuración
2. O usar el header `Cookie` manualmente si es necesario

### Ejemplos de Requests para Insomnia:

#### Request 1: POST Register
- **URL**: `http://localhost:8080/api/auth/register`
- **Method**: POST
- **Headers**: `Content-Type: application/json`
- **Body** (JSON):
```json
{
  "username": "testuser",
  "password": "password123",
  "email": "testuser@test.com"
}
```

#### Request 2: POST Login
- **URL**: `http://localhost:8080/api/auth/login`
- **Method**: POST
- **Headers**: `Content-Type: application/json`
- **Body** (JSON):
```json
{
  "username": "testuser",
  "password": "password123"
}
```

#### Request 3: GET Status
- **URL**: `http://localhost:8080/api/auth/status`
- **Method**: GET
- **Headers**: Ninguno necesario (usa cookies de sesión)

#### Request 4: POST Logout
- **URL**: `http://localhost:8080/api/auth/logout`
- **Method**: POST
- **Headers**: Ninguno necesario (usa cookies de sesión)

#### Request 5: GET Users
- **URL**: `http://localhost:8080/api/auth/users`
- **Method**: GET
- **Headers**: Ninguno necesario (usa cookies de sesión)

## Respuestas de Error Comunes

### Error de validación:
```json
{
  "success": false,
  "message": "El nombre de usuario es obligatorio"
}
```

### Usuario ya existe:
```json
{
  "success": false,
  "message": "El nombre de usuario ya está en uso"
}
```

### Credenciales inválidas:
```json
{
  "success": false,
  "message": "Credenciales inválidas"
}
```

### Sin sesión activa:
```json
{
  "success": false,
  "message": "No hay sesión activa"
}
```

## Consideraciones de Seguridad

- Contraseñas hasheadas con BCrypt
- Validación de entrada con Bean Validation
- Gestión de sesiones HTTP
- Endpoints de autenticación sin autenticación previa requerida
