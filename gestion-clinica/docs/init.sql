-- ============================================================
-- Inicialización de bases de datos - Proyecto Gestión Clínica
-- Este archivo será ejecutado por el contenedor MySQL al iniciar
-- por primera vez, siguiendo el modelo de despliegue tipo DoggySpa.
-- ============================================================

-- Crear bases de datos utilizadas por los microservicios
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

-- Aplicar cambios de permisos
FLUSH PRIVILEGES;