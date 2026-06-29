@echo off
title Gestion Clinica - Backup Base de Datos
cls

echo ==========================================================
echo        RESPALDO BASE DE DATOS GESTION CLINICA
echo ==========================================================
echo.

echo Verificando contenedores...
docker compose ps

echo.
echo Creando carpeta backups si no existe...
if not exist backups mkdir backups

echo.
echo Generando nombre de archivo con fecha y hora...
set FECHA=%date:~6,4%-%date:~3,2%-%date:~0,2%
set HORA=%time:~0,2%-%time:~3,2%-%time:~6,2%
set HORA=%HORA: =0%
set BACKUP_FILE=backups\backup_gestion_clinica_%FECHA%_%HORA%.sql

echo.
echo Archivo de respaldo:
echo %BACKUP_FILE%
echo.

echo Generando respaldo de todas las bases de datos...
docker exec gestion-clinica-mysql mysqldump -u root -proot --all-databases > %BACKUP_FILE%
if errorlevel 1 (
    echo ERROR: No se pudo generar el respaldo. Verifica que el contenedor gestion-clinica-mysql este en ejecucion.
    pause
    exit /b 1
)

echo.
echo ==========================================================
echo RESPALDO FINALIZADO
echo ==========================================================
echo Archivo generado:
echo %BACKUP_FILE%
echo.
pause
