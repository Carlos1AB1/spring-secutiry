# Proyecto Spring Security - Sistema de Autenticación y Autorización

## Descripción
Este proyecto es una implementación completa de un sistema de autenticación desarrollado con Spring Boot y Spring Security. El sistema proporciona funcionalidades de registro de usuarios, inicio de sesión, verificación por correo electrónico y autenticación basada en JWT (JSON Web Tokens).

## Características principales

- ✅ Registro de usuarios con validación de campos
- ✅ Sistema de verificación de cuenta por correo electrónico
- ✅ Autenticación segura mediante JWT
- ✅ Protección de rutas basada en autenticación
- ✅ Configuración CORS para permitir solicitudes de orígenes específicos
- ✅ Encriptación de contraseñas con BCrypt
- ✅ Diseño de correos electrónicos atractivos para la verificación

## Estructura del proyecto

```
├── config
│   ├── ApplicationConfiguration.java
│   ├── EmailConfiguration.java
│   ├── JwtAuthenticationFilter.java
│   └── SecurityConfiguration.java
├── controller
│   ├── AuthenticationController.java
│   └── UserController.java
├── dto
│   ├── LoginUserDto.java
│   ├── RegisterUserDto.java
│   └── VerifyUserDto.java
├── model
│   └── User.java
├── repository
│   └── UserRepository.java
├── responses
│   └── LoginResponse.java
├── service
│   ├── AuthenticationService.java
│   ├── EmailService.java
│   ├── JwtService.java
│   └── UserService.java
└── DemoApplication.java
```

## Requisitos previos

- Java 17 o superior
- Maven
- Una cuenta de Gmail para el envío de correos electrónicos (o configurar otro proveedor SMTP)

## Configuración

### Configuración de variables de entorno

Crea un archivo `.env` en la raíz del proyecto con la siguiente estructura:

```
# Configuración de MySQL en XAMPP
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/demo_auth_db?useSSL=false&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=

# Configuración de JWT (generada con alta seguridad para producción)
JWT_SECRET_KEY=XVQ7GhPzSLFqSMI1O8gTzXcw0Dcx0uHDMn9DIpNa2ZLCvfoIXC7XufF3yqt1GnKE2VvEEwOBFJrNW5g2t1Nj6g==

# Configuración de correo (SMTP)
SUPPORT_EMAIL=tucorreo@gmail.com
APP_PASSWORD=tu_contraseña
```

### Configuración de propiedades

El archivo `application.properties` o `application.yml` en `src/main/resources` debe configurarse para leer las variables de entorno:

```properties
# Configuración de base de datos
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Configuración de JWT
security.jwt.secret-key=${JWT_SECRET_KEY}
security.jwt.expiration-time=86400000

# Configuración de correo electrónico
spring.mail.username=${SUPPORT_EMAIL}
spring.mail.password=${APP_PASSWORD}
```

> **Nota**: Para Gmail, debes usar una "contraseña de aplicación" en lugar de tu contraseña normal. Puedes generar una en la configuración de seguridad de tu cuenta de Google.

### Clave secreta JWT

La clave secreta JWT debe ser una cadena codificada en Base64. Puedes generar una con el siguiente comando:

```bash
openssl rand -base64 64
```

## Endpoints API

### Autenticación

| Método | URL | Descripción | Cuerpo de la solicitud |
|--------|-----|-------------|------------------------|
| POST | `/auth/signup` | Registro de usuario | `{"email": "...", "password": "...", "username": "..."}` |
| POST | `/auth/login` | Inicio de sesión | `{"email": "...", "password": "..."}` |
| POST | `/auth/verify` | Verificación de cuenta | `{"email": "...", "verificationCode": "..."}` |
| POST | `/auth/resend` | Reenvío del código de verificación | `{"email": "..."}` (como parámetro de consulta) |

### Usuarios

| Método | URL | Descripción | Autenticación requerida |
|--------|-----|-------------|-------------------------|
| GET | `/users/me` | Obtener información del usuario autenticado | Sí |
| GET | `/users/` | Obtener lista de todos los usuarios | Sí |

## Flujo de funcionamiento

1. **Registro de usuario**:
   - El usuario envía sus datos (correo electrónico, nombre de usuario y contraseña).
   - El sistema crea una cuenta deshabilitada y genera un código de verificación de 6 dígitos.
   - Se envía un correo electrónico de verificación con el código.

2. **Verificación de cuenta**:
   - El usuario recibe el código de verificación y lo envía junto con su correo electrónico.
   - El sistema verifica el código y activa la cuenta si es correcto.

3. **Inicio de sesión**:
   - El usuario proporciona su correo electrónico y contraseña.
   - El sistema verifica las credenciales y que la cuenta esté verificada.
   - Se genera un token JWT y se devuelve al usuario.

4. **Autenticación en peticiones**:
   - El cliente incluye el token JWT en el encabezado `Authorization` de las solicitudes.
   - El filtro `JwtAuthenticationFilter` valida el token y establece la autenticación.

## Seguridad

- Las contraseñas se almacenan cifradas utilizando BCrypt.
- Los tokens JWT están firmados con una clave secreta.
- Las rutas están protegidas, excepto las de autenticación (`/auth/**`).
- CORS está configurado para permitir solicitudes solo desde orígenes específicos.

## Personalización del correo electrónico

El correo electrónico de verificación utiliza HTML y CSS para crear una experiencia visual atractiva con un diseño moderno. El código incluye:

- Esquema de colores con gradientes y efectos de resplandor
- Animaciones para el código de verificación
- Diseño responsivo

## Posibles mejoras

- Implementar recuperación de contraseñas
- Añadir roles y permisos de usuario
- Implementar autenticación de doble factor
- Añadir límites de intentos de inicio de sesión y bloqueo de cuentas
- Mejorar el manejo de errores y validaciones

## Contribuciones

Las contribuciones son bienvenidas. Por favor, sigue estos pasos:

1. Haz un fork del repositorio
2. Crea una rama para tu función (`git checkout -b feature/nueva-funcionalidad`)
3. Haz commit de tus cambios (`git commit -am 'Añadir nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crea un nuevo Pull Request

## Licencia

[MIT](LICENSE)
