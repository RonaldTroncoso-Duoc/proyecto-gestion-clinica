@echo off
title Gestion Clinica - Logs Docker
cls

echo ==========================================================
echo        LOGS DE GESTION CLINICA CON DOCKER
echo ==========================================================
echo.
echo Mostrando logs de todos los servicios.
echo Para salir de la visualizacion usa Ctrl + C.
echo Esto no detiene los contenedores.
echo.

docker compose logs -f

pause
