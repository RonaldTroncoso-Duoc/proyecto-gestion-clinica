# 🐳 Puesta en marcha con Docker — Gestión Clínica

## Proyecto académico — Sistema de microservicios Spring Boot

---

## 1. Descripción general

Este documento explica cómo ejecutar el ecosistema de microservicios **Gestión Clínica** utilizando **Docker** y **Docker Compose**.

La puesta en marcha con Docker permite levantar todos los componentes necesarios del sistema en contenedores independientes, sin instalar MySQL ni ejecutar manualmente cada archivo `.jar`.

El sistema considera los siguientes componentes:

```text
MySQL
Eureka Server
API Gateway
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

---

## 2. Objetivo del despliegue

El objetivo del despliegue es ejecutar Gestión Clínica como un sistema distribuido de microservicios.

Docker Compose se encarga de:

```text
Crear el contenedor MySQL.
Ejecutar Eureka Server.
Ejecutar todos los microservicios Spring Boot desde archivos JAR.
Ejecutar API Gateway como punto de entrada principal.
Crear una red interna común.
Configurar variables de entorno para comunicación entre contenedores.
Persistir datos de MySQL mediante volumen Docker.
Ejecutar docs/init.sql al inicializar MySQL por primera vez.
```

---

## 3. Importante: el ZIP no es una imagen Docker completa

El paquete de despliegue **no corresponde a una imagen Docker cerrada**.

Corresponde a una carpeta de despliegue que contiene:

```text
Archivos .jar compilados.
Archivo docker-compose.yml.
Archivo .env.
Archivo docs/init.sql.
Scripts .bat de operación.
Documentación de despliegue.
```

El archivo principal es:

```text
docker-compose.yml
```

Este archivo define qué contenedores se crean, qué imagen usan, qué JAR ejecutan, qué puertos exponen, qué variables utilizan y cómo se comunican internamente.

---

## 4. Requisito obligatorio: Docker Desktop abierto

Antes de levantar el sistema, Docker Desktop debe estar instalado, abierto y funcionando.

Verificar desde terminal:

```bash
docker --version
docker compose version
docker info
```

Si aparece un error como:

```text
Cannot connect to the Docker daemon
```

o:

```text
open //./pipe/DockerDesktopLinuxEngine: The system cannot find the file specified
```

significa que Docker Desktop no está abierto o su motor todavía no terminó de iniciar.

Solución:

```text
1. Abrir Docker Desktop.
2. Esperar a que el motor de Docker quede iniciado.
3. Volver a ejecutar los comandos de verificación.
4. Levantar el sistema con docker compose up -d o arrancar-docker.bat.
```

---

## 4.1. Requisitos previos

Antes de ejecutar el sistema se debe contar con:

```text
Docker Desktop instalado.
Docker Desktop abierto y funcionando.
Carpeta gestion-clinica/ con los archivos de despliegue.
Carpeta apps/ con los archivos .jar compilados.
Archivo docker-compose.yml.
Archivo .env.
Carpeta docs/ con el archivo init.sql.
Carpeta backups/ para respaldos de base de datos.
Scripts .bat de operación para Windows.
```

En laboratorio o ambiente académico normalmente Docker Desktop ya se encuentra instalado. Aun así, siempre se debe comprobar que esté abierto antes de ejecutar Docker Compose.

---

## 4.2. Verificación de Docker

Abrir CMD, PowerShell o la terminal integrada de Visual Studio Code y ejecutar:

```bash
docker --version
```

Resultado esperado, puede variar según la versión instalada:

```text
Docker version 29.x.x, build xxxxxxx
```

Verificar Docker Compose:

```bash
docker compose version
```

Resultado esperado, puede variar según la versión instalada:

```text
Docker Compose version v2.x.x
```

Verificar que el motor de Docker esté activo:

```bash
docker info
```

Si los comandos responden correctamente, Docker está disponible para levantar el ecosistema.

Nota para Windows:

```text
En CMD se puede usar: cd gestion-clinica && docker compose config
En PowerShell se recomienda usar: cd gestion-clinica; docker compose config
```

---

## 5. Estructura esperada del despliegue

La carpeta principal del proyecto debe contener:

```text
gestion-clinica/
├── apps/
│   ├── api-gateway.jar
│   ├── eureka-server.jar
│   ├── ms-auth.jar
│   ├── ms-pacientes.jar
│   ├── ms-medicos.jar
│   ├── ms-citas.jar
│   ├── ms-especialidades.jar
│   ├── ms-horarios.jar
│   ├── ms-fichas-clinicas.jar
│   ├── ms-recetas.jar
│   ├── ms-pagos.jar
│   └── ms-notificaciones.jar
├── backups/
├── docs/
│   └── init.sql
├── .env
├── docker-compose.yml
├── arrancar-docker.bat
├── detener-docker.bat
├── ver-logs.bat
├── backup-db.bat
├── restaurar-db.bat
└── README-DESPLIEGUE-DOCKER.md
```

---

## 6. Archivos JAR requeridos

La carpeta `apps/` debe contener exactamente estos archivos:

```text
api-gateway.jar
eureka-server.jar
ms-auth.jar
ms-pacientes.jar
ms-medicos.jar
ms-citas.jar
ms-especialidades.jar
ms-horarios.jar
ms-fichas-clinicas.jar
ms-recetas.jar
ms-pagos.jar
ms-notificaciones.jar
```

Los nombres deben coincidir con las rutas montadas en `docker-compose.yml`.

Si falta un JAR, el contenedor correspondiente no podrá iniciar y puede aparecer un error similar a:

```text
Unable to access jarfile /app/app.jar
```

---

## 7. Imagen Java utilizada

Este proyecto no usa Dockerfiles ni imágenes personalizadas.

Cada servicio Spring Boot se ejecuta con la imagen oficial:

```yaml
image: eclipse-temurin:21-jre
```

Cada JAR se monta dentro del contenedor así:

```yaml
volumes:
  - ./apps/ms-auth.jar:/app/app.jar:ro
command: ["java", "-jar", "/app/app.jar"]
```

Esto permite ejecutar los JAR ya compilados sin Maven ni código fuente dentro del contenedor.

---

## 8. Archivo `.env`

El archivo `.env` centraliza variables utilizadas por Docker Compose.

Variables principales:

```env
MYSQL_ROOT_PASSWORD=root
MYSQL_USER=root
MYSQL_PASSWORD=root
MYSQL_HOST=mysql
MYSQL_PORT=3306

DB_AUTH=db_auth
DB_PACIENTES=db_pacientes
DB_MEDICOS=db_medicos
DB_CITAS=db_citas
DB_ESPECIALIDADES=db_especialidades
DB_HORARIOS=db_horarios
DB_FICHAS_CLINICAS=db_fichas_clinicas
DB_RECETAS=db_recetas
DB_PAGOS=db_pagos
DB_NOTIFICACIONES=db_notificaciones

EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
JWT_SECRET=EstaEsUnaLlaveMuySecretaYQueDebeSerLargaParaQueFuncione12345
```

En Docker, los microservicios no usan `localhost` para MySQL ni Eureka. Usan nombres de servicios Docker:

```text
mysql
eureka-server
```

---

## 9. Archivo `docs/init.sql`

El archivo:

```text
docs/init.sql
```

crea las bases de datos requeridas por cada microservicio:

```sql
CREATE DATABASE IF NOT EXISTS db_auth;
CREATE DATABASE IF NOT EXISTS db_pacientes;
CREATE DATABASE IF NOT EXISTS db_medicos;
CREATE DATABASE IF NOT EXISTS db_citas;
CREATE DATABASE IF NOT EXISTS db_especialidades;
CREATE DATABASE IF NOT EXISTS db_horarios;
CREATE DATABASE IF NOT EXISTS db_fichas_clinicas;
CREATE DATABASE IF NOT EXISTS db_recetas;
CREATE DATABASE IF NOT EXISTS db_pagos;
CREATE DATABASE IF NOT EXISTS db_notificaciones;
```

Este archivo **no crea tablas** y **no inserta datos**.

Las tablas son creadas automáticamente por Hibernate mediante:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Los datos semilla son insertados por los `DataInitializer` de cada microservicio.

Importante:

```text
init.sql solo se ejecuta automáticamente cuando MySQL inicializa un volumen nuevo.
Si el volumen mysql-data ya existe, init.sql no se vuelve a ejecutar.
```

---

## 10. Componentes y puertos

| Componente         | Descripción                            | Puerto externo |
| ------------------ | -------------------------------------- | -------------: |
| MySQL              | Base de datos                          |           3307 |
| Eureka Server      | Registro y descubrimiento de servicios |           8761 |
| API Gateway        | Punto de entrada principal             |           8080 |
| ms-auth            | Autenticación y usuarios               |           8081 |
| ms-pacientes       | Gestión de pacientes                   |           8082 |
| ms-medicos         | Gestión de médicos                     |           8083 |
| ms-citas           | Gestión de citas                       |           8084 |
| ms-especialidades  | Gestión de especialidades              |           8085 |
| ms-horarios        | Gestión de horarios médicos            |           8086 |
| ms-fichas-clinicas | Fichas clínicas                        |           8087 |
| ms-recetas         | Recetas médicas                        |           8088 |
| ms-pagos           | Pagos                                  |           8089 |
| ms-notificaciones  | Notificaciones                         |           8090 |

---

## 11. Orden lógico de arranque

El orden lógico del ecosistema es:

```text
1. MySQL
2. Eureka Server
3. Microservicios raíz
   - ms-auth
   - ms-especialidades
   - ms-pacientes
   - ms-medicos
4. Microservicios dependientes
   - ms-horarios
   - ms-citas
   - ms-fichas-clinicas
   - ms-recetas
   - ms-pagos
   - ms-notificaciones
5. API Gateway
```

Docker Compose maneja estas dependencias mediante `depends_on`.

MySQL incluye `healthcheck`, por lo que los microservicios esperan a que MySQL esté saludable antes de iniciar.

---

## 12. Red Docker

Todos los servicios utilizan una red común:

```yaml
gestion-clinica-net
```

Esto permite comunicación interna por nombre de servicio:

```text
mysql:3306
eureka-server:8761
ms-auth:8081
ms-pacientes:8082
```

---

## 13. Volumen de MySQL

MySQL utiliza un volumen persistente:

```yaml
mysql-data
```

Montado en:

```yaml
mysql-data:/var/lib/mysql
```

Esto permite conservar los datos aunque los contenedores se detengan o se creen nuevamente.

No usar:

```bash
docker compose down -v
```

si se desea conservar la base de datos.

---

## 14. Levantar el sistema completo

Desde la carpeta `gestion-clinica/` ejecutar:

```bash
docker compose up -d
```

O usar el script:

```text
arrancar-docker.bat
```

Este script:

```text
Verifica Docker.
Levanta los contenedores.
Espera unos segundos.
Muestra docker compose ps.
Informa URLs principales.
```

---

## 15. Revisar estado de contenedores

Ejecutar:

```bash
docker compose ps
```

Estado esperado:

```text
gestion-clinica-mysql              running / healthy
gestion-clinica-eureka-server      running
gestion-clinica-ms-auth            running
gestion-clinica-ms-pacientes       running
gestion-clinica-ms-medicos         running
gestion-clinica-ms-especialidades  running
gestion-clinica-ms-horarios        running
gestion-clinica-ms-citas           running
gestion-clinica-ms-fichas-clinicas running
gestion-clinica-ms-recetas         running
gestion-clinica-ms-pagos           running
gestion-clinica-ms-notificaciones  running
gestion-clinica-api-gateway        running
```

---

## 16. Accesos principales

Una vez levantado el sistema:

```text
API Gateway:
http://localhost:8080

Swagger UI centralizado:
http://localhost:8080/swagger-ui.html

Eureka Server:
http://localhost:8761

MySQL:
localhost:3307
```

Nota importante:

```text
Estos accesos funcionan mientras Docker Desktop esté corriendo y los contenedores estén activos.
Si Docker se detiene, el sistema deja de estar disponible.
```

---

## 16.1. Disponibilidad del sistema para usuarios

Para que otros usuarios o herramientas puedan consumir la API de Gestión Clínica, los contenedores Docker deben permanecer en ejecución.

Mientras el sistema esté levantado con:

```bash
docker compose up -d
```

los servicios quedan corriendo en segundo plano.

El sistema estará disponible mientras:

```text
Docker Desktop esté abierto.
Los contenedores estén en estado running.
MySQL esté en estado healthy.
El computador o servidor permanezca encendido.
Los puertos necesarios estén disponibles.
La red permita conexiones hacia el equipo donde corre Docker.
```

Si se ejecuta:

```bash
docker compose down
```

el sistema se detiene y los usuarios ya no podrán consumir la API.

Frase clave:

```text
Docker debe permanecer corriendo para que el sistema esté disponible.
```

---

## 16.2. Importante sobre ejecución local con localhost

En esta guía los accesos se realizan usando `localhost`, por ejemplo:

```text
http://localhost:8080
http://localhost:8761
http://localhost:8080/swagger-ui.html
```

Esto significa que el sistema está disponible desde el mismo computador donde se está ejecutando Docker.

Si otro usuario intenta acceder desde otro computador usando `localhost`, no llegará al sistema Gestión Clínica, porque `localhost` siempre apunta al propio equipo desde donde se escribe la dirección.

---

## 16.3. Acceso desde otros equipos de la red

Si se desea que otros equipos de la misma red consuman la API, no deben usar `localhost`.

Deben usar la IP del computador o servidor donde está corriendo Docker.

Ejemplo:

```text
http://192.168.1.50:8080
```

En este caso:

```text
192.168.1.50
```

representa la IP del equipo que ejecuta Docker.

Los accesos quedarían, por ejemplo:

```text
API Gateway:
http://192.168.1.50:8080

Swagger UI centralizado:
http://192.168.1.50:8080/swagger-ui.html

Eureka Server:
http://192.168.1.50:8761
```

Importante:

```text
El firewall de Windows debe permitir conexiones a los puertos utilizados.
La red debe permitir conexiones hacia el equipo que ejecuta Docker.
Los puertos deben estar correctamente expuestos en docker-compose.yml.
El computador donde corre Docker debe permanecer encendido.
Docker Desktop debe permanecer iniciado.
```

---

## 16.4. Uso de dominio o publicación de API

Este proyecto no incluye frontend web. Por lo tanto, no existe una URL de frontend que deba modificarse.

Si en un escenario futuro se publica la API usando un dominio, se deben reemplazar las direcciones locales por el dominio correspondiente.

Ejemplo:

```text
http://localhost:8080
```

podría publicarse como:

```text
https://api.gestion-clinica.cl
```

En ese caso se deben revisar especialmente:

```text
1. Configuración DNS del dominio.
2. Configuración de firewall del servidor.
3. Puertos expuestos públicamente.
4. Configuración del API Gateway.
5. Configuración CORS si clientes externos consumirán la API.
6. Certificados HTTPS, si se usa dominio seguro.
7. Proxy inverso, si corresponde, por ejemplo Nginx, Apache o Traefik.
8. Variables de entorno o clientes externos que apunten al Gateway.
```

---

## 16.5. Docker en un servidor

Para un uso más real, Docker no debería depender del computador personal de un estudiante.

Lo ideal es ejecutar Docker en un servidor o máquina destinada al despliegue.

Ese servidor debe permanecer encendido y con Docker activo.

Flujo recomendado:

```text
1. Copiar la carpeta gestion-clinica/ al servidor.
2. Abrir Docker Desktop o Docker Engine.
3. Verificar docker --version y docker compose version.
4. Ejecutar docker compose up -d.
5. Verificar docker compose ps.
6. Revisar Eureka y Swagger UI.
7. Publicar el acceso mediante IP o dominio, si corresponde.
8. Mantener el servidor encendido para que la API esté disponible.
9. Configurar respaldos periódicos de la base de datos.
```

---

## 17. Rutas principales mediante API Gateway

| Recurso         | Ruta Gateway                                   |
| --------------- | ---------------------------------------------- |
| Auth            | `http://localhost:8080/api/auth/**`            |
| Pacientes       | `http://localhost:8080/api/pacientes/**`       |
| Médicos         | `http://localhost:8080/api/medicos/**`         |
| Especialidades  | `http://localhost:8080/api/especialidades/**`  |
| Horarios        | `http://localhost:8080/api/horarios/**`        |
| Citas           | `http://localhost:8080/api/citas/**`           |
| Fichas clínicas | `http://localhost:8080/api/fichas-clinicas/**` |
| Recetas         | `http://localhost:8080/api/recetas/**`         |
| Pagos           | `http://localhost:8080/api/pagos/**`           |
| Notificaciones  | `http://localhost:8080/api/notificaciones/**`  |

---

## 18. Swagger / OpenAPI

El API Gateway centraliza Swagger UI en:

```text
http://localhost:8080/swagger-ui.html
```

También se pueden revisar documentos OpenAPI por servicio mediante rutas del gateway:

```text
http://localhost:8080/ms-auth/v3/api-docs
http://localhost:8080/ms-pacientes/v3/api-docs
http://localhost:8080/ms-medicos/v3/api-docs
http://localhost:8080/ms-citas/v3/api-docs
```

---

## 19. Revisar logs

Para revisar logs de todos los servicios:

```bash
docker compose logs -f
```

O ejecutar:

```text
ver-logs.bat
```

Para un servicio específico:

```bash
docker compose logs -f ms-auth
docker compose logs -f ms-citas
docker compose logs -f api-gateway
```

Para salir:

```text
Ctrl + C
```

Esto no detiene los contenedores.

---

## 20. Detener el sistema

Para detener los contenedores sin eliminar datos:

```bash
docker compose down
```

O ejecutar:

```text
detener-docker.bat
```

---

## 21. Reiniciar desde cero

Si se requiere reiniciar todo desde una base MySQL completamente vacía:

```bash
docker compose down -v
docker compose up -d
```

Advertencia:

```text
docker compose down -v elimina el volumen mysql-data y borra los datos persistentes.
```

Al crear nuevamente el volumen, MySQL volverá a ejecutar `docs/init.sql`.

---

## 21.1. ¿Se pierden los datos si se reinicia el computador?

Si el sistema está configurado con volumen Docker, los datos no deberían perderse solo por reiniciar el computador.

Después de reiniciar el PC o servidor, se debe:

```text
1. Abrir Docker Desktop.
2. Entrar a la carpeta gestion-clinica/.
3. Ejecutar docker compose up -d.
4. Verificar docker compose ps.
```

Los datos deberían seguir disponibles mientras el volumen `mysql-data` no haya sido eliminado.

En un ambiente real siempre se recomienda generar respaldos periódicos.

---

## 21.2. Cuándo sí se pueden perder los datos

Los datos pueden perderse en los siguientes casos:

```text
Se ejecuta docker compose down -v.
Se elimina el volumen desde Docker Desktop.
Se borra manualmente el volumen Docker.
Se cambia la configuración del volumen.
Se recrea el entorno desde cero sin respaldo.
Se daña el equipo servidor o se elimina la instalación de Docker.
```

El siguiente comando elimina contenedores y también volúmenes asociados:

```bash
docker compose down -v
```

Advertencia:

```text
No usar docker compose down -v si se desea conservar la base de datos.
```

---

## 22. Respaldo de base de datos

Para generar un respaldo:

```text
backup-db.bat
```

El respaldo se guarda en:

```text
backups/
```

El script usa el contenedor:

```text
gestion-clinica-mysql
```

Y ejecuta:

```bash
mysqldump -u root -proot --all-databases
```

---

## 23. Restaurar respaldo

Para restaurar un archivo `.sql`:

```text
restaurar-db.bat
```

El script muestra los respaldos disponibles en `backups/` y solicita el nombre del archivo.

Advertencia:

```text
Restaurar un respaldo puede sobrescribir información existente.
Antes de restaurar, se recomienda generar un respaldo del estado actual.
```

---

## 23.1. Recomendación de respaldo automático

Aunque los datos se conserven mediante un volumen Docker, se recomienda respaldar periódicamente la base de datos.

Opciones recomendadas:

```text
Crear respaldos automáticos con backup-db.bat.
Programar el respaldo con el Programador de tareas de Windows.
Guardar respaldos en una carpeta externa al proyecto.
Sincronizar respaldos con OneDrive, Google Drive u otro servicio.
Mantener respaldos históricos por fecha.
Probar periódicamente restaurar un respaldo en un ambiente de prueba.
```

Ejemplo de nombres de respaldo:

```text
backup_gestion_clinica_2026-06-22_09-52-00.sql
backup_gestion_clinica_2026-06-23_09-52-00.sql
backup_gestion_clinica_2026-06-24_09-52-00.sql
```

---

## 24. Scripts disponibles

| Script                | Función                                          |
| --------------------- | ------------------------------------------------ |
| `arrancar-docker.bat` | Levanta el ecosistema con `docker compose up -d` |
| `detener-docker.bat`  | Detiene contenedores sin eliminar volúmenes      |
| `ver-logs.bat`        | Muestra logs de todos los servicios              |
| `backup-db.bat`       | Genera respaldo SQL de todas las bases           |
| `restaurar-db.bat`    | Restaura respaldo SQL desde `backups/`           |

---

## 25. Verificación en Eureka

Abrir:

```text
http://localhost:8761
```

Se espera visualizar servicios registrados como:

```text
API-GATEWAY
MS-AUTH
MS-PACIENTES
MS-MEDICOS
MS-ESPECIALIDADES
MS-HORARIOS
MS-CITAS
MS-FICHAS-CLINICAS
MS-RECETAS
MS-PAGOS
MS-NOTIFICACIONES
```

---

## 25.1. Pruebas de endpoints directos

Los microservicios también pueden probarse directamente por sus puertos publicados.

Ejemplos:

```text
Auth:
http://localhost:8081/api/auth/login
http://localhost:8081/api/auth/register

Pacientes:
http://localhost:8082/api/pacientes/activos
http://localhost:8082/api/pacientes/{id}

Médicos:
http://localhost:8083/api/medicos/activos
http://localhost:8083/api/medicos/{id}

Citas:
http://localhost:8084/api/citas/{id}
http://localhost:8084/api/citas/paciente/{pacienteId}

Especialidades:
http://localhost:8085/api/especialidades/activas
http://localhost:8085/api/especialidades/{id}

Horarios:
http://localhost:8086/api/horarios/disponibles
http://localhost:8086/api/horarios/{id}

Fichas clínicas:
http://localhost:8087/api/fichas-clinicas/activas
http://localhost:8087/api/fichas-clinicas/{id}

Recetas:
http://localhost:8088/api/recetas/activas
http://localhost:8088/api/recetas/{id}

Pagos:
http://localhost:8089/api/pagos/{id}
http://localhost:8089/api/pagos/paciente/{pacienteId}

Notificaciones:
http://localhost:8090/api/notificaciones/{id}
http://localhost:8090/api/notificaciones/paciente/{pacienteId}
```

Importante:

```text
Varias rutas requieren token JWT y roles específicos.
Para probar rutas protegidas primero se debe iniciar sesión en /api/auth/login.
```

---

## 25.2. Pruebas mediante API Gateway

La forma recomendada de consumir el sistema es mediante el API Gateway:

```text
http://localhost:8080
```

Ejemplos:

```text
http://localhost:8080/api/auth/login
http://localhost:8080/api/auth/register
http://localhost:8080/api/pacientes/activos
http://localhost:8080/api/medicos/activos
http://localhost:8080/api/especialidades/activas
http://localhost:8080/api/horarios/disponibles
http://localhost:8080/api/citas/{id}
http://localhost:8080/api/fichas-clinicas/{id}
http://localhost:8080/api/recetas/activas
http://localhost:8080/api/pagos/{id}
http://localhost:8080/api/notificaciones/{id}
```

Rutas públicas principales:

```text
POST /api/auth/login
POST /api/auth/register
POST /api/pacientes/register
```

El resto de rutas pasa por el filtro de autenticación del Gateway y requiere JWT válido.

---

## 26. Errores comunes

### Docker Desktop no está abierto

Solución:

```text
Abrir Docker Desktop y esperar a que el motor esté activo.
```

### Puerto ocupado

Mensaje posible:

```text
port is already allocated
```

Solución:

```text
Cerrar el proceso que usa el puerto o cambiar el puerto externo en docker-compose.yml.
```

### Microservicio no conecta a MySQL

Revisar:

```text
Que mysql esté healthy.
Que SPRING_DATASOURCE_URL apunte a mysql:3306.
Que la contraseña coincida con MYSQL_ROOT_PASSWORD.
```

### Microservicio no aparece en Eureka

Revisar:

```text
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
```

### API Gateway no enruta

Revisar:

```text
Que el microservicio destino esté registrado en Eureka.
Que el nombre coincida con spring.application.name.
Revisar logs de api-gateway.
```

### `init.sql` no se ejecutó

Causa probable:

```text
El volumen mysql-data ya existía.
```

Solución si se desea reiniciar todo:

```bash
docker compose down -v
docker compose up -d
```

### El archivo `.jar` no existe

Mensaje posible:

```text
Unable to access jarfile /app/app.jar
```

Causa:

```text
El archivo .jar no existe en apps/ o tiene un nombre distinto al definido en docker-compose.yml.
```

Solución:

```text
Verificar que apps/ contenga todos los .jar requeridos.
Revisar que los nombres coincidan exactamente con docker-compose.yml.
Renombrar el .jar o ajustar la ruta en docker-compose.yml.
```

### El sistema funciona localmente, pero otros usuarios no pueden acceder

Posibles causas:

```text
Los usuarios están usando localhost en sus computadores.
El firewall bloquea el acceso.
El computador donde corre Docker no está en la misma red.
Los puertos no están expuestos.
Docker no está corriendo.
El computador servidor está apagado.
```

Solución:

```text
Usar la IP del equipo donde corre Docker.
Revisar firewall de Windows.
Verificar docker compose ps.
Mantener Docker Desktop abierto y los contenedores running.
```

### El sistema funciona con IP, pero no con dominio

Posibles causas:

```text
El dominio no apunta al servidor correcto.
No está configurado el DNS.
No está configurado el proxy inverso.
Falta configurar HTTPS o certificados.
Falta revisar CORS para clientes externos.
El API Gateway no está publicado correctamente.
```

Solución:

```text
Revisar DNS.
Revisar proxy inverso.
Revisar firewall.
Revisar certificados HTTPS.
Revisar configuración CORS y API Gateway.
```

### Se reinició el PC y el sistema no está disponible

Causa:

```text
El computador se reinició.
Docker Desktop no se abrió automáticamente.
Los contenedores no fueron levantados nuevamente.
```

Solución:

```text
Abrir Docker Desktop.
Entrar a la carpeta gestion-clinica/.
Ejecutar docker compose up -d.
Verificar docker compose ps.
```

Los datos deberían seguir disponibles si el volumen `mysql-data` no fue eliminado.

### Se perdieron datos de MySQL

Posibles causas:

```text
Se ejecutó docker compose down -v.
Se eliminó el volumen desde Docker Desktop.
Se recreó el entorno desde cero.
Se restauró un respaldo antiguo.
Se borró la instalación o datos de Docker.
```

Solución:

```text
Revisar si existe un respaldo en backups/.
Ejecutar restaurar-db.bat.
Seleccionar el archivo .sql a restaurar.
```

---

## 27. Evidencia recomendada para evaluación

Guardar capturas de:

```text
docker --version
docker compose version
docker compose ps
MySQL healthy
Eureka con servicios registrados
API Gateway funcionando
Swagger UI centralizado
Endpoint de autenticación funcionando
Endpoint de negocio funcionando mediante Gateway
Logs de un microservicio iniciado correctamente
Carpeta backups con respaldo generado
Prueba desde otro equipo usando IP del servidor, si corresponde
Respaldo restaurado correctamente en ambiente de prueba, si corresponde
```

---

## 28. Comandos principales

| Acción                   | Comando                  |
| ------------------------ | ------------------------ |
| Ver Docker               | `docker --version`       |
| Ver Docker Compose       | `docker compose version` |
| Ver estado Docker        | `docker info`            |
| Validar Compose          | `docker compose config`  |
| Levantar sistema         | `docker compose up -d`   |
| Ver contenedores         | `docker compose ps`      |
| Ver logs                 | `docker compose logs -f` |
| Detener sistema          | `docker compose down`    |
| Detener y borrar volumen | `docker compose down -v` |
| Respaldar base           | `backup-db.bat`          |
| Restaurar base           | `restaurar-db.bat`       |

---

## 29. Estado final esperado

Al finalizar correctamente el despliegue:

```text
MySQL debe estar healthy.
Eureka debe estar disponible en http://localhost:8761.
Los microservicios deben registrarse en Eureka.
API Gateway debe estar disponible en http://localhost:8080.
Swagger UI debe estar disponible desde el gateway.
Los DataInitializer deben poblar datos semilla automáticamente.
Los datos deben persistir mientras no se elimine el volumen mysql-data.
Los respaldos deben poder generarse en la carpeta backups/.
El sistema debe poder consumirse desde otro equipo usando la IP del servidor, si la red y firewall lo permiten.
```

---

## 30. Conclusión

La puesta en marcha con Docker permite ejecutar Gestión Clínica como un ecosistema completo de microservicios sin instalar MySQL ni ejecutar cada JAR manualmente.

Docker Compose orquesta los contenedores, la red interna, los volúmenes, los puertos y las variables de entorno necesarias.

Frases clave:

```text
Docker no reemplaza Spring Boot; entrega el entorno donde Spring Boot se ejecuta.
El ZIP no es una imagen Docker completa; contiene los archivos para que Docker Compose levante el sistema.
Docker Desktop debe estar abierto antes de ejecutar el sistema.
Docker debe permanecer corriendo para que la API esté disponible.
localhost solo funciona desde el equipo donde corre Docker.
Para otros equipos se debe usar la IP o dominio del servidor.
MySQL conserva datos mediante volumen Docker.
init.sql crea bases de datos solo la primera vez que se inicializa el volumen.
Los datos semilla son cargados por los DataInitializer de cada microservicio.
No usar docker compose down -v si se desea conservar la información.
La base de datos debe respaldarse periódicamente para evitar pérdida de información.
```
