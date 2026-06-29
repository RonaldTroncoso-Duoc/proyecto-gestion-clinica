@echo off
title Gestion Clinica - Arranque Docker
cls

echo ==========================================================
echo        INICIANDO GESTION CLINICA CON DOCKER
echo ==========================================================
echo.

echo Verificando Docker...
docker --version
if errorlevel 1 (
    echo ERROR: Docker no esta disponible. Verifica que Docker Desktop este instalado y abierto.
    pause
    exit /b 1
)

docker compose version
if errorlevel 1 (
    echo ERROR: Docker Compose no esta disponible.
    pause
    exit /b 1
)

echo.
echo Levantando contenedores...
docker compose up -d
if errorlevel 1 (
    echo ERROR: No se pudieron levantar los contenedores.
    pause
    exit /b 1
)

echo.
echo Esperando unos segundos para que los servicios inicialicen...
timeout /t 15 /nobreak > nul

echo.
echo Estado de los contenedores:
docker compose ps

echo.
echo ==========================================================
echo        SISTEMA INICIADO
echo ==========================================================
echo.
echo Accesos principales:
echo API Gateway:   http://localhost:8080
echo Swagger UI:    http://localhost:8080/swagger-ui.html
echo Eureka Server: http://localhost:8761
echo MySQL:         localhost:3307
echo.
echo Para revisar logs ejecuta: ver-logs.bat
echo Para detener el sistema ejecuta: detener-docker.bat
echo.
pause