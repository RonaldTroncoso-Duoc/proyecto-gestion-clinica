# Gestión Clínica - Sistema de Microservicios

Gestión Clínica es un sistema académico de microservicios desarrollado con **Spring Boot** para administrar procesos principales de una clínica: autenticación, pacientes, médicos, especialidades, horarios, citas, fichas clínicas, recetas, pagos y notificaciones.

El proyecto está diseñado para trabajar arquitectura de microservicios, **Eureka Server**, **API Gateway**, **OpenFeign**, **MySQL**, **JWT**, **Spring Security**, **Swagger/OpenAPI** y estructura **Maven padre-hijos**.

Este README está orientado al **desarrollo local**, es decir, ejecutar los servicios desde el código fuente usando Maven, VSCode o Spring Boot Dashboard.

---

# 1. Objetivo del proyecto

El sistema permite gestionar un flujo clínico básico:

1. Registrar y autenticar usuarios.
2. Registrar pacientes.
3. Registrar médicos.
4. Administrar especialidades médicas.
5. Gestionar horarios disponibles de médicos.
6. Crear y administrar citas médicas.
7. Registrar fichas clínicas asociadas a citas.
8. Emitir recetas médicas.
9. Registrar pagos asociados a pacientes y citas.
10. Generar notificaciones relacionadas con citas o eventos clínicos.

---

# 2. Arquitectura general

```text
Cliente externo / Postman / Navegador / Swagger UI
        |
        v
API Gateway :8080
        |
        +--> ms-auth             :8081 -> db_auth
        +--> ms-pacientes        :8082 -> db_pacientes
        +--> ms-medicos          :8083 -> db_medicos
        +--> ms-citas            :8084 -> db_citas
        +--> ms-especialidades   :8085 -> db_especialidades
        +--> ms-horarios         :8086 -> db_horarios
        +--> ms-fichas-clinicas  :8087 -> db_fichas_clinicas
        +--> ms-recetas          :8088 -> db_recetas
        +--> ms-pagos            :8089 -> db_pagos
        +--> ms-notificaciones   :8090 -> db_notificaciones

Eureka Server :8761
MySQL local   :3307
```

Cada microservicio mantiene su propia base de datos y se registra en Eureka para ser descubierto por el API Gateway y por otros servicios mediante OpenFeign.

---

# 3. Microservicios del sistema

| Módulo               | Puerto | Responsabilidad                          |
| -------------------- | -----: | ---------------------------------------- |
| `eureka-server`      |   8761 | Registro y descubrimiento de servicios   |
| `api-gateway`        |   8080 | Punto único de entrada a las APIs        |
| `ms-auth`            |   8081 | Autenticación, usuarios, roles y JWT     |
| `ms-pacientes`       |   8082 | Administración de pacientes              |
| `ms-medicos`         |   8083 | Administración de médicos                |
| `ms-citas`           |   8084 | Administración de citas médicas          |
| `ms-especialidades`  |   8085 | Administración de especialidades médicas |
| `ms-horarios`        |   8086 | Administración de horarios médicos       |
| `ms-fichas-clinicas` |   8087 | Administración de fichas clínicas        |
| `ms-recetas`         |   8088 | Administración de recetas médicas        |
| `ms-pagos`           |   8089 | Administración de pagos                  |
| `ms-notificaciones`  |   8090 | Administración de notificaciones         |

---

# 4. Tecnologías utilizadas

- Java 21
- Spring Boot 3.5.13
- Spring Cloud 2025.0.2
- Eureka Server
- Eureka Client
- Spring Cloud Gateway
- OpenFeign
- Spring Web
- Spring WebFlux en Gateway
- Spring Data JPA
- Spring Security
- JWT con JJWT
- MySQL
- Maven
- Lombok
- Bean Validation
- Swagger / OpenAPI con Springdoc
- VSCode
- Spring Boot Dashboard

---

# 5. Estructura del proyecto

El proyecto usa un `pom.xml` padre en la raíz y módulos Maven hijos con doble anidación.

```text
gestion-clinica/
|
├── pom.xml
├── README-DESARROLLO.md
├── README-DESPLIEGUE-DOCKER.md
|
├── docs/
│   └── init.sql
|
├── eureka-server/
│   └── eureka-server/
│       ├── pom.xml
│       └── src/
|
├── api-gateway/
│   └── api-gateway/
│       ├── pom.xml
│       └── src/
|
├── ms-auth/
│   └── ms-auth/
│       ├── pom.xml
│       └── src/
|
├── ms-pacientes/
│   └── ms-pacientes/
│       ├── pom.xml
│       └── src/
|
├── ms-medicos/
│   └── ms-medicos/
│       ├── pom.xml
│       └── src/
|
├── ms-citas/
│   └── ms-citas/
│       ├── pom.xml
│       └── src/
|
├── ms-especialidades/
│   └── ms-especialidades/
│       ├── pom.xml
│       └── src/
|
├── ms-horarios/
│   └── ms-horarios/
│       ├── pom.xml
│       └── src/
|
├── ms-fichas-clinicas/
│   └── ms-fichas-clinicas/
│       ├── pom.xml
│       └── src/
|
├── ms-recetas/
│   └── ms-recetas/
│       ├── pom.xml
│       └── src/
|
├── ms-pagos/
│   └── ms-pagos/
│       ├── pom.xml
│       └── src/
|
└── ms-notificaciones/
    └── ms-notificaciones/
        ├── pom.xml
        └── src/
```

---

# 6. Bases de datos

El proyecto usa una base de datos independiente por microservicio.

| Microservicio        | Base de datos        | Dominio principal              |
| -------------------- | -------------------- | ------------------------------ |
| `ms-auth`            | `db_auth`            | usuarios, roles, autenticación |
| `ms-pacientes`       | `db_pacientes`       | pacientes                      |
| `ms-medicos`         | `db_medicos`         | médicos                        |
| `ms-citas`           | `db_citas`           | citas médicas                  |
| `ms-especialidades`  | `db_especialidades`  | especialidades                 |
| `ms-horarios`        | `db_horarios`        | horarios                       |
| `ms-fichas-clinicas` | `db_fichas_clinicas` | fichas clínicas                |
| `ms-recetas`         | `db_recetas`         | recetas                        |
| `ms-pagos`           | `db_pagos`           | pagos                          |
| `ms-notificaciones`  | `db_notificaciones`  | notificaciones                 |

El script base de creación de bases se encuentra en:

```text
docs/init.sql
```

En desarrollo local, las tablas son creadas automáticamente por Hibernate mediante:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Además, cada microservicio posee un `DataInitializer` para poblar datos semilla cuando corresponde.

---

# 7. Configuración de MySQL local

Los microservicios están configurados para usar MySQL local en el puerto:

```text
3307
```

Ejemplo real de configuración:

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/db_auth?createDatabaseIfNotExist=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

Antes de ejecutar los servicios se debe tener MySQL activo. Puede ser mediante XAMPP, MySQL instalado localmente o un contenedor Docker solo para base de datos.

Si el equipo usa MySQL en el puerto `3306`, se debe modificar la URL en cada `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/NOMBRE_BD?createDatabaseIfNotExist=true&serverTimezone=UTC
```

Si MySQL local tiene contraseña, se debe ajustar:

```properties
spring.datasource.password=TU_PASSWORD
```

---

# 8. Orden de ejecución

Antes de levantar los microservicios, se debe iniciar MySQL.

Luego se recomienda ejecutar los servicios en este orden:

| Orden | Servicio             | Puerto |
| ----: | -------------------- | -----: |
|     1 | `eureka-server`      |   8761 |
|     2 | `ms-auth`            |   8081 |
|     3 | `ms-especialidades`  |   8085 |
|     4 | `ms-pacientes`       |   8082 |
|     5 | `ms-medicos`         |   8083 |
|     6 | `ms-horarios`        |   8086 |
|     7 | `ms-citas`           |   8084 |
|     8 | `ms-fichas-clinicas` |   8087 |
|     9 | `ms-recetas`         |   8088 |
|    10 | `ms-pagos`           |   8089 |
|    11 | `ms-notificaciones`  |   8090 |
|    12 | `api-gateway`        |   8080 |

El Gateway debe iniciarse al final para que pueda enrutar correctamente hacia servicios ya registrados en Eureka.

---

# 9. Ejecución desde VSCode

Se recomienda usar la extensión **Spring Boot Dashboard** de VSCode.

Desde el panel de Spring Boot se pueden iniciar los servicios uno por uno en el orden indicado:

```text
eureka-server
ms-auth
ms-especialidades
ms-pacientes
ms-medicos
ms-horarios
ms-citas
ms-fichas-clinicas
ms-recetas
ms-pagos
ms-notificaciones
api-gateway
```

También se pueden ejecutar desde terminal.

Ejemplo:

```bash
cd eureka-server/eureka-server
mvn spring-boot:run
```

Ejemplo para un microservicio:

```bash
cd ms-auth/ms-auth
mvn spring-boot:run
```

---

# 10. Compilación del proyecto completo

Desde la raíz `gestion-clinica/`:

```bash
mvn clean install -DskipTests
```

Se usa `-DskipTests` porque en proyectos de microservicios los tests generados automáticamente pueden requerir configuración adicional de contexto, base de datos, Eureka, seguridad o perfiles de prueba.

Más adelante se pueden implementar pruebas unitarias y de integración con JUnit, Mockito, MockMvc, WebTestClient, Testcontainers o H2.

---

# 11. Compilación de módulos individuales

Compilar solo un módulo:

```bash
mvn clean install -pl ms-auth/ms-auth -DskipTests
```

Compilar un módulo junto a dependencias necesarias del reactor Maven:

```bash
mvn clean install -pl ms-auth/ms-auth -am -DskipTests
```

Ejemplos:

```bash
mvn clean install -pl api-gateway/api-gateway -DskipTests
mvn clean install -pl ms-pacientes/ms-pacientes -DskipTests
mvn clean install -pl ms-citas/ms-citas -DskipTests
```

---

# 12. Eureka Server

La consola de Eureka se encuentra en:

```text
http://localhost:8761
```

Cuando todos los servicios están levantados, deben aparecer registrados:

```text
API-GATEWAY
MS-AUTH
MS-PACIENTES
MS-MEDICOS
MS-CITAS
MS-ESPECIALIDADES
MS-HORARIOS
MS-FICHAS-CLINICAS
MS-RECETAS
MS-PAGOS
MS-NOTIFICACIONES
```

---

# 13. API Gateway

El API Gateway permite consumir todos los microservicios desde el puerto:

```text
http://localhost:8080
```

Rutas principales:

| Recurso         | URL                                            |
| --------------- | ---------------------------------------------- |
| Auth            | `http://localhost:8080/api/auth/**`            |
| Pacientes       | `http://localhost:8080/api/pacientes/**`       |
| Médicos         | `http://localhost:8080/api/medicos/**`         |
| Citas           | `http://localhost:8080/api/citas/**`           |
| Especialidades  | `http://localhost:8080/api/especialidades/**`  |
| Horarios        | `http://localhost:8080/api/horarios/**`        |
| Fichas clínicas | `http://localhost:8080/api/fichas-clinicas/**` |
| Recetas         | `http://localhost:8080/api/recetas/**`         |
| Pagos           | `http://localhost:8080/api/pagos/**`           |
| Notificaciones  | `http://localhost:8080/api/notificaciones/**`  |

Rutas públicas principales:

```text
POST /api/auth/login
POST /api/auth/register
POST /api/pacientes/register
```

Las demás rutas pasan por `AuthenticationFilter` y requieren token JWT.

---

# 14. Swagger / OpenAPI

El Gateway centraliza Swagger UI en:

```text
http://localhost:8080/swagger-ui.html
```

Documentos OpenAPI por servicio mediante Gateway:

```text
http://localhost:8080/ms-auth/v3/api-docs
http://localhost:8080/ms-citas/v3/api-docs
http://localhost:8080/ms-especialidades/v3/api-docs
http://localhost:8080/ms-fichas-clinicas/v3/api-docs
http://localhost:8080/ms-horarios/v3/api-docs
http://localhost:8080/ms-medicos/v3/api-docs
http://localhost:8080/ms-notificaciones/v3/api-docs
http://localhost:8080/ms-pacientes/v3/api-docs
http://localhost:8080/ms-pagos/v3/api-docs
http://localhost:8080/ms-recetas/v3/api-docs
```

También se puede revisar Swagger directamente por microservicio si está habilitado:

```text
http://localhost:8081/swagger-ui.html
http://localhost:8082/swagger-ui.html
http://localhost:8083/swagger-ui.html
```

En algunos servicios puede estar configurado como:

```text
http://localhost:8082/doc/swagger-ui.html
```

---

# 15. Seguridad y autenticación

El sistema utiliza **Spring Security** y **JWT**.

La clave JWT se configura en `application.properties`:

```properties
jwt.secret=EstaEsUnaLlaveMuySecretaYQueDebeSerLargaParaQueFuncione12345
```

Flujo general:

1. El usuario se registra o inicia sesión en `ms-auth`.
2. `ms-auth` genera un token JWT.
3. El cliente envía el token en las siguientes solicitudes.
4. El Gateway valida el token con `AuthenticationFilter`.
5. Los microservicios aplican reglas de autorización con `@PreAuthorize`.

Formato del header:

```http
Authorization: Bearer TOKEN_JWT
```

Roles usados en el sistema:

```text
ADMIN
DOCTOR
PACIENTE
```

Nota: en algunos controladores aparece `PATIENT`; se recomienda mantener consistencia usando `PACIENTE` en futuras revisiones.

---

# 16. Comunicación entre microservicios

El proyecto usa **OpenFeign** para comunicación entre servicios.

| Servicio origen      | Servicio destino     | Objetivo principal                     |
| -------------------- | -------------------- | -------------------------------------- |
| `ms-pacientes`       | `ms-auth`            | Registrar usuario asociado al paciente |
| `ms-medicos`         | `ms-auth`            | Registrar usuario asociado al médico   |
| `ms-medicos`         | `ms-especialidades`  | Validar especialidad del médico        |
| `ms-horarios`        | `ms-medicos`         | Validar existencia del médico          |
| `ms-citas`           | `ms-pacientes`       | Validar existencia del paciente        |
| `ms-citas`           | `ms-medicos`         | Validar existencia del médico          |
| `ms-citas`           | `ms-horarios`        | Validar, ocupar o liberar horario      |
| `ms-fichas-clinicas` | `ms-citas`           | Validar cita asociada                  |
| `ms-fichas-clinicas` | `ms-pacientes`       | Validar paciente                       |
| `ms-fichas-clinicas` | `ms-medicos`         | Validar médico                         |
| `ms-recetas`         | `ms-fichas-clinicas` | Validar ficha clínica                  |
| `ms-recetas`         | `ms-pacientes`       | Validar paciente                       |
| `ms-recetas`         | `ms-medicos`         | Validar médico                         |
| `ms-pagos`           | `ms-citas`           | Validar cita                           |
| `ms-pagos`           | `ms-pacientes`       | Validar paciente                       |
| `ms-notificaciones`  | `ms-citas`           | Validar cita                           |
| `ms-notificaciones`  | `ms-pacientes`       | Validar paciente                       |

---

# 17. Flujo funcional principal

## Paso 1: Registrar usuario administrador o iniciar sesión

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json
```

```json
{
  "username": "admin",
  "password": "admin123"
}
```

La respuesta debe entregar un token JWT para consumir rutas protegidas.

## Paso 2: Registrar paciente

```http
POST http://localhost:8080/api/pacientes/register
Content-Type: application/json
```

```json
{
  "run": "12345678-9",
  "nombre": "Laura",
  "apellido": "Fuentes",
  "email": "laura.fuentes@correo.cl",
  "telefono": "+56912345678",
  "fechaNacimiento": "1995-05-10",
  "direccion": "La Florida 123"
}
```

## Paso 3: Crear especialidad

```http
POST http://localhost:8080/api/especialidades
Authorization: Bearer TOKEN_JWT
Content-Type: application/json
```

```json
{
  "nombre": "Cardiología",
  "descripcion": "Especialidad médica cardiovascular"
}
```

## Paso 4: Crear médico

```http
POST http://localhost:8080/api/medicos
Authorization: Bearer TOKEN_JWT
Content-Type: application/json
```

```json
{
  "run": "11111111-1",
  "nombre": "Carlos",
  "apellido": "Rojas",
  "email": "carlos.rojas@clinica.cl",
  "telefono": "+56911111111",
  "especialidadId": 1
}
```

## Paso 5: Crear horario

```http
POST http://localhost:8080/api/horarios
Authorization: Bearer TOKEN_JWT
Content-Type: application/json
```

```json
{
  "medicoId": 1,
  "fecha": "2026-07-01",
  "horaInicio": "09:00:00",
  "horaFin": "09:30:00"
}
```

## Paso 6: Crear cita

```http
POST http://localhost:8080/api/citas
Authorization: Bearer TOKEN_JWT
Content-Type: application/json
```

```json
{
  "pacienteId": 1,
  "medicoId": 1,
  "horarioId": 1,
  "motivo": "Control general"
}
```

## Paso 7: Crear ficha clínica

```http
POST http://localhost:8080/api/fichas-clinicas
Authorization: Bearer TOKEN_JWT
Content-Type: application/json
```

```json
{
  "pacienteId": 1,
  "medicoId": 1,
  "citaId": 1,
  "motivoConsulta": "Dolor de cabeza",
  "diagnostico": "Cefalea tensional",
  "tratamiento": "Reposo e hidratación",
  "observaciones": "Control en 7 días"
}
```

## Paso 8: Crear receta

```http
POST http://localhost:8080/api/recetas
Authorization: Bearer TOKEN_JWT
Content-Type: application/json
```

```json
{
  "fichaClinicaId": 1,
  "pacienteId": 1,
  "medicoId": 1,
  "medicamento": "Paracetamol",
  "dosis": "500 mg cada 8 horas",
  "indicaciones": "Tomar con agua después de comidas",
  "duracionDias": 3
}
```

## Paso 9: Registrar pago

```http
POST http://localhost:8080/api/pagos
Authorization: Bearer TOKEN_JWT
Content-Type: application/json
```

```json
{
  "pacienteId": 1,
  "citaId": 1,
  "monto": 25000,
  "metodoPago": "efectivo"
}
```

## Paso 10: Crear notificación

```http
POST http://localhost:8080/api/notificaciones
Authorization: Bearer TOKEN_JWT
Content-Type: application/json
```

```json
{
  "pacienteId": 1,
  "citaId": 1,
  "tipo": "RECORDATORIO",
  "mensaje": "Recuerde asistir a su cita médica"
}
```

---

# 18. Validaciones implementadas

## `ms-auth`

- Username obligatorio.
- Username entre 4 y 50 caracteres.
- Email obligatorio y con formato válido.
- Password obligatoria.
- Password entre 6 y 100 caracteres.

## `ms-pacientes`

- RUN obligatorio.
- Nombre y apellido obligatorios.
- Email obligatorio y válido.
- Teléfono con formato `+56` y 12 caracteres.
- Fecha de nacimiento obligatoria y pasada.
- Dirección con máximo 200 caracteres.

## `ms-medicos`

- RUN obligatorio.
- Nombre y apellido obligatorios.
- Email obligatorio y válido.
- Teléfono con formato `+56` y 12 caracteres.
- Especialidad obligatoria.
- Valida especialidad mediante `ms-especialidades`.

## `ms-especialidades`

- Nombre obligatorio.
- Nombre entre 3 y 100 caracteres.
- Descripción con máximo 255 caracteres.

## `ms-horarios`

- Médico obligatorio.
- Fecha obligatoria y no anterior a la actual.
- Hora de inicio obligatoria.
- Hora de término obligatoria.
- Valida médico mediante `ms-medicos`.

## `ms-citas`

- Paciente obligatorio.
- Médico obligatorio.
- Horario obligatorio.
- Motivo obligatorio.
- Motivo entre 3 y 200 caracteres.
- Valida paciente, médico y horario con Feign.

## `ms-fichas-clinicas`

- Paciente obligatorio.
- Médico obligatorio.
- Cita obligatoria.
- Motivo, diagnóstico y tratamiento obligatorios.
- Valida cita, paciente y médico con Feign.

## `ms-recetas`

- Ficha clínica obligatoria.
- Paciente obligatorio.
- Médico obligatorio.
- Medicamento, dosis e indicaciones obligatorias.
- Duración mínima de 1 día.

## `ms-pagos`

- Paciente obligatorio.
- Cita obligatoria.
- Monto obligatorio y mayor a 0.
- Método de pago obligatorio.
- Valida paciente y cita con Feign.

## `ms-notificaciones`

- Paciente obligatorio.
- Cita obligatoria.
- Tipo de notificación obligatorio.
- Mensaje obligatorio.
- Valida paciente y cita con Feign.

---

# 19. Manejo de errores

Los microservicios incorporan manejo centralizado de errores mediante clases `GlobalExceptionHandler` con `@RestControllerAdvice` o `@ControllerAdvice`.

Ejemplo de respuesta de error esperada:

```json
{
  "timestamp": "2026-06-29T18:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Existen campos inválidos en la solicitud",
  "path": "/api/pacientes"
}
```

La estructura exacta puede variar según el microservicio.

---

# 20. Logs

El proyecto utiliza logs con Lombok:

```java
@Slf4j
```

Ejemplos de uso:

```java
log.info("Creando paciente");
log.warn("Registro no encontrado");
log.error("Error al comunicarse con otro microservicio", ex);
```

En desarrollo, los logs permiten revisar:

- Inicio de servicios.
- Registro en Eureka.
- Creación de datos semilla.
- Errores de conexión a MySQL.
- Errores de comunicación Feign.
- Validación de JWT.

---

# 21. Comandos útiles

## Compilar todo el proyecto

```bash
mvn clean install -DskipTests
```

## Ejecutar Eureka

```bash
cd eureka-server/eureka-server
mvn spring-boot:run
```

## Ejecutar API Gateway

```bash
cd api-gateway/api-gateway
mvn spring-boot:run
```

## Ejecutar un microservicio

```bash
cd ms-pacientes/ms-pacientes
mvn spring-boot:run
```

## Compilar solo un módulo

```bash
mvn clean install -pl ms-pacientes/ms-pacientes -DskipTests
```

## Compilar un módulo y sus dependencias

```bash
mvn clean install -pl ms-pacientes/ms-pacientes -am -DskipTests
```

## Verificar versión de Java

```bash
java -version
```

Debe usarse Java 21.

## Verificar versión de Maven

```bash
mvn -version
```

---

# 22. Documentación adicional

Documentación complementaria disponible:

```text
docs/init.sql
README-DESPLIEGUE-DOCKER.md
```

El archivo `README-DESPLIEGUE-DOCKER.md` explica cómo ejecutar el sistema empaquetado mediante Docker Compose.

---

# 23. Estado actual del proyecto

| Elemento                         | Estado        |
| -------------------------------- | ------------- |
| Proyecto padre Maven             | Implementado  |
| Microservicios Spring Boot       | Implementados |
| Bases de datos por microservicio | Implementadas |
| Eureka Server                    | Implementado  |
| API Gateway                      | Implementado  |
| Swagger centralizado             | Implementado  |
| OpenFeign                        | Implementado  |
| Seguridad JWT                    | Implementada  |
| Roles y autorización             | Implementados |
| DataInitializer                  | Implementado  |
| Manejo de errores                | Implementado  |
| Logs                             | Implementado  |
| Docker Compose                   | Implementado  |
| Frontend web                     | No aplica     |
| Testing automatizado completo    | Pendiente     |

---

# 24. Próximas mejoras sugeridas

- Crear colección Postman o Bruno con ejemplos de uso.
- Unificar formato de respuestas de error entre microservicios.
- Revisar consistencia de roles `PACIENTE` vs `PATIENT`.
- Agregar perfiles `dev`, `docker` y `test`.
- Implementar pruebas unitarias con JUnit y Mockito.
- Implementar pruebas de controller con MockMvc o WebTestClient.
- Implementar pruebas de integración con Testcontainers.
- Agregar H2 para pruebas automatizadas simples.
- Mejorar documentación de endpoints por microservicio.
- Automatizar generación de JAR para despliegue Docker.

---

# 25. Resumen de desarrollo local

```text
1. Iniciar MySQL local en puerto 3307.
2. Verificar Java 21 y Maven.
3. Compilar con mvn clean install -DskipTests.
4. Ejecutar Eureka Server.
5. Ejecutar microservicios de negocio.
6. Ejecutar API Gateway al final.
7. Revisar Eureka en http://localhost:8761.
8. Revisar Swagger en http://localhost:8080/swagger-ui.html.
9. Iniciar sesión en /api/auth/login.
10. Consumir rutas protegidas usando Authorization: Bearer TOKEN_JWT.
```
