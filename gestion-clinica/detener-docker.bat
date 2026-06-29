@echo off
title Gestion Clinica - Detener Docker
cls

echo ==========================================================
echo        DETENIENDO GESTION CLINICA CON DOCKER
echo ==========================================================
echo.

docker compose down

echo.
echo Sistema detenido.
echo Los datos de MySQL se conservan porque no se eliminaron volumenes.
echo.
echo Si necesitas reiniciar completamente la base de datos, elimina manualmente el volumen mysql-data.
echo No uses este script para borrar datos.
echo.
pause