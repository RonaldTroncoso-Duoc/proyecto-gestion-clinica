@echo off
title Gestion Clinica - Restaurar Base de Datos
cls

echo ==========================================================
echo        RESTAURAR BASE DE DATOS GESTION CLINICA
echo ==========================================================
echo.

if not exist backups (
    echo ERROR: No existe la carpeta backups.
    echo Genera primero un respaldo con backup-db.bat o crea la carpeta backups manualmente.
    pause
    exit /b 1
)

echo Archivos disponibles en backups:
echo.
dir backups\*.sql
echo.

set /p BACKUP_FILE=Escribe el nombre del archivo .sql a restaurar: 

if not exist backups\%BACKUP_FILE% (
    echo.
    echo ERROR: El archivo backups\%BACKUP_FILE% no existe.
    pause
    exit /b 1
)

echo.
echo Restaurando base de datos desde:
echo backups\%BACKUP_FILE%
echo.

docker exec -i gestion-clinica-mysql mysql -u root -proot < backups\%BACKUP_FILE%
if errorlevel 1 (
    echo ERROR: No se pudo restaurar la base de datos. Verifica que el contenedor gestion-clinica-mysql este en ejecucion.
    pause
    exit /b 1
)

echo.
echo ==========================================================
echo RESTAURACION FINALIZADA
echo ==========================================================
echo.
pause
