# Sistema Gestión Clínica — Entrega Final

Proyecto académico basado en **arquitectura de microservicios con Spring Boot**, **Maven multi-módulo**, **Eureka Server**, **API Gateway**, **MySQL**, **Docker Compose**, **Swagger/OpenAPI** y pruebas unitarias con **JUnit 5 / Mockito**.

---

## 1. Enlaces externos de entrega

Los enlaces principales de descarga y revisión se dejan al inicio del documento para facilitar la evaluación.

### Versión Docker del sistema

Descarga del paquete Docker:

```text
https://drive.google.com/file/d/1WTJo4QfRSc7aanlhJLOe3OLd_ysnMWEH/view?usp=sharing
```

### Video de defensa

Enlace del video de defensa:

```text
PENDIENTE: incorporar enlace del video de defensa aquí.
```

---

## 2. Descripción general del proyecto

Gestión Clínica es un sistema distribuido compuesto por microservicios independientes. Su objetivo es administrar procesos principales de una clínica, incluyendo autenticación, pacientes, médicos, especialidades, horarios, citas, fichas clínicas, recetas, pagos y notificaciones.

El sistema está compuesto por:

```text
Eureka Server
API Gateway
MySQL
ms-auth
ms-pacientes
ms-medicos
ms-especialidades
ms-horarios
ms-citas
ms-fichas-clinicas
ms-recetas
ms-pagos
ms-notificaciones
```

Cada microservicio posee su propia responsabilidad y base de datos, siguiendo una separación por dominio.

---

## 3. Arquitectura general

```text
Cliente externo / Postman / Swagger UI
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
MySQL Docker  :3307 externo / 3306 interno
```

---

## 4. Puesta en marcha del sistema

La entrega principal del proyecto está orientada a ejecución **dockerizada** mediante Docker Desktop y Docker Compose.

No es necesario ejecutar manualmente los microservicios de forma nativa en orden jerárquico, ya que `docker-compose.yml` define el ecosistema completo, incluyendo MySQL, Eureka Server, microservicios, API Gateway, red Docker, volumen persistente, variables de entorno, healthcheck y dependencias de arranque.

### Orden lógico de arranque

Aunque Docker Compose automatiza el proceso, el orden lógico respetado por la configuración es:

```text
1. MySQL
2. Eureka Server
3. Microservicios de negocio
4. API Gateway
```

En una ejecución nativa tradicional el orden jerárquico sería:

```text
1. Eureka Server
2. Microservicios
3. API Gateway
```

Para esta entrega se prioriza la ejecución dockerizada, porque permite levantar el sistema completo de forma reproducible con un solo comando.

### Comando principal

Desde la carpeta:

```text
gestion-clinica/
```

ejecutar:

```bash
docker compose up -d
```

También se puede usar el script Windows incluido en la entrega Docker:

```text
arrancar-docker.bat
```

La guía completa de puesta en marcha con Docker se encuentra en:

```text
gestion-clinica/README-DESPLIEGUE-DOCKER.md
```

---

## 5. Servicios y puertos

| Servicio           | Puerto | Descripción                                 |
| ------------------ | -----: | ------------------------------------------- |
| Eureka Server      |   8761 | Registro y descubrimiento de microservicios |
| API Gateway        |   8080 | Punto único de entrada al sistema           |
| MySQL              |   3307 | Base de datos expuesta al host              |
| ms-auth            |   8081 | Autenticación, usuarios, roles y JWT        |
| ms-pacientes       |   8082 | Gestión de pacientes                        |
| ms-medicos         |   8083 | Gestión de médicos                          |
| ms-citas           |   8084 | Gestión de citas médicas                    |
| ms-especialidades  |   8085 | Gestión de especialidades                   |
| ms-horarios        |   8086 | Gestión de horarios médicos                 |
| ms-fichas-clinicas |   8087 | Gestión de fichas clínicas                  |
| ms-recetas         |   8088 | Gestión de recetas médicas                  |
| ms-pagos           |   8089 | Gestión de pagos                            |
| ms-notificaciones  |   8090 | Gestión de notificaciones                   |

---

## 6. Calidad, pruebas y documentación técnica

### Maven multi-módulo

El proyecto utiliza un `pom.xml` padre ubicado en:

```text
gestion-clinica/pom.xml
```

Desde ese archivo se administran los módulos principales:

```text
api-gateway
eureka-server
ms-auth
ms-pacientes
ms-medicos
ms-citas
ms-especialidades
ms-horarios
ms-fichas-clinicas
ms-recetas
ms-pagos
ms-notificaciones
```

También se centralizan versiones importantes como Java 21, Spring Boot, Spring Cloud, Springdoc OpenAPI, JJWT y Lombok.

### Suite de pruebas unitarias

El proyecto incorpora pruebas unitarias orientadas a la capa de servicios usando:

```text
JUnit 5
Mockito
AssertJ
Spring Boot Test
```

Las pruebas validan comportamientos principales como listado de entidades, creación de registros, actualización, activación/desactivación lógica, validación de duplicados, manejo de errores de negocio, llamadas a repositorios y comunicación simulada con clientes Feign.

Para ejecutar la compilación y la suite de pruebas desde la raíz del proyecto:

```bash
cd gestion-clinica
mvn clean install
```

Si se requiere compilar omitiendo pruebas para generar artefactos rápidamente:

```bash
mvn clean install -DskipTests
```

### Swagger / OpenAPI

El proyecto documenta endpoints mediante **Swagger/OpenAPI** usando Springdoc.

Swagger centralizado desde el API Gateway:

```text
http://localhost:8080/swagger-ui.html
```

Documentos OpenAPI expuestos por el Gateway:

```text
http://localhost:8080/ms-auth/v3/api-docs
http://localhost:8080/ms-pacientes/v3/api-docs
http://localhost:8080/ms-medicos/v3/api-docs
http://localhost:8080/ms-citas/v3/api-docs
http://localhost:8080/ms-especialidades/v3/api-docs
http://localhost:8080/ms-horarios/v3/api-docs
http://localhost:8080/ms-fichas-clinicas/v3/api-docs
http://localhost:8080/ms-recetas/v3/api-docs
http://localhost:8080/ms-pagos/v3/api-docs
http://localhost:8080/ms-notificaciones/v3/api-docs
```

---

## 7. Seguridad

El sistema utiliza autenticación y autorización basada en **Spring Security**, **JWT** y roles.

Roles principales:

```text
ADMIN
DOCTOR
PACIENTE
```

Flujo general:

1. El usuario inicia sesión en `ms-auth`.
2. El sistema genera un token JWT.
3. El cliente envía el token mediante el header `Authorization`.
4. El API Gateway y los microservicios validan acceso según rutas y roles.

Formato:

```http
Authorization: Bearer TOKEN_JWT
```

---

## 8. Documentación complementaria

Dentro del proyecto se incluyen documentos específicos:

```text
gestion-clinica/README-DESPLIEGUE-DOCKER.md
gestion-clinica/README-DESARROLLO.md
```

| Documento                     | Propósito                                                                     |
| ----------------------------- | ----------------------------------------------------------------------------- |
| `README-DESPLIEGUE-DOCKER.md` | Guía completa para levantar el sistema con Docker Compose                     |
| `README-DESARROLLO.md`        | Guía técnica para comprender arquitectura, módulos, ejecución local y pruebas |

---

## 9. Video de defensa

Apartado rellenable para completar una vez finalizado el video.

### Enlace del video

```text
PENDIENTE: agregar enlace final del video de defensa.
```

### Duración

```text
PENDIENTE: indicar duración final del video.
```

Referencia de evaluación:

```text
Duración ideal: 15 minutos.
Duración máxima recomendada: 18 minutos.
```

### Subtítulos

Indicar una de las siguientes opciones:

```text
PENDIENTE: el video incluye subtítulos integrados.
```

o:

```text
PENDIENTE: se adjunta archivo subtitulos-video.txt.
```

Archivo esperado en caso de usar subtítulos externos:

```text
subtitulos-video.txt
```

---

## 10. Resumen para evaluación

```text
El proyecto implementa una arquitectura de microservicios con Spring Boot.
La ejecución principal de entrega es dockerizada mediante Docker Compose.
El sistema usa Eureka Server para descubrimiento de servicios.
El API Gateway centraliza el acceso a los microservicios.
MySQL se ejecuta en contenedor con volumen persistente.
Cada microservicio maneja su propio dominio y base de datos.
La documentación técnica está separada entre desarrollo y despliegue Docker.
Los endpoints están documentados con Swagger/OpenAPI.
La suite de pruebas unitarias se ejecuta con Maven usando mvn clean install.
```
