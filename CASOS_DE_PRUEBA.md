# CASOS DE PRUEBA

+--------+-------------------+--------------------------------------------+-------------------------------+------------------------------------------------+-----------------------------------------------------+--------------+
|  ID    | Funcionalidad     | Caso de Prueba                             | Precondición                  | Datos de Entrada                                | Resultado Esperado                                  | Tipo         |
+--------+-------------------+--------------------------------------------+-------------------------------+------------------------------------------------+-----------------------------------------------------+--------------+
| TC001  | Registro           | Registro exitoso                           | Usuario no registrado         | Nombre de usuario válido, contraseña válida     | HTTP 200 OK, mensaje "Registro exitoso"             | Positiva     |
| TC002  | Registro           | Registro con nombre de usuario existente  | Usuario ya registrado         | Nombre duplicado, contraseña válida             | HTTP 409 Conflict, "Nombre de usuario ya registrado" | Negativa     |
| TC003  | Registro           | Registro con datos inválidos              | Usuario no registrado         | Nombre vacío, contraseña corta                  | HTTP 400 Bad Request, error de validación           | Negativa     |
| TC004  | Login              | Login exitoso                              | Usuario registrado            | Nombre y contraseña correctos                   | HTTP 200 OK, token de autenticación                 | Positiva     |
| TC005  | Login              | Login con contraseña incorrecta           | Usuario registrado            | Nombre válido, contraseña incorrecta            | HTTP 401 Unauthorized, "Credenciales inválidas"     | Negativa     |
| TC006  | Login              | Login con usuario inexistente             | Usuario no registrado         | Nombre no registrado, contraseña cualquiera     | HTTP 404 Not Found, "Usuario no encontrado"         | Negativa     |
| TC007  | Logout             | Logout exitoso                             | Usuario logueado              | Token válido                                    | HTTP 200 OK, mensaje "Logout exitoso"               | Positiva     |
| TC008  | Logout             | Logout con token inválido                 | Usuario logueado              | Token mal formado o inexistente                 | HTTP 401 Unauthorized, "Token inválido"             | Negativa     |
| TC009  | Logout             | Logout sin token                          | Usuario logueado              | Sin encabezado Authorization                    | HTTP 400 Bad Request, "Token requerido"             | Negativa     |
+--------+-------------------+--------------------------------------------+-------------------------------+------------------------------------------------+-----------------------------------------------------+--------------+

