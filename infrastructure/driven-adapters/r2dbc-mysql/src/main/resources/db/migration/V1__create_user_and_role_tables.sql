-- V1__create_user_and_role_tables.sql

CREATE TABLE Rol (
  UniqueID INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(50) NOT NULL UNIQUE,
  descripcion VARCHAR(255) NULL,
  PRIMARY KEY (UniqueID)
);

CREATE TABLE Usuario (
  id_usuario INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(50) NOT NULL,
  apellido VARCHAR(50) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  documento_identidad VARCHAR(50) NOT NULL,
  id_rol INT NOT NULL,
  salario_base DECIMAL(10, 2) NOT NULL,
  telefono VARCHAR(20) NULL,
  direccion VARCHAR(255) NULL,
  fecha_nacimiento DATE NULL,
  PRIMARY KEY (id_usuario),
  CONSTRAINT fk_usuario_rol
    FOREIGN KEY (id_rol)
    REFERENCES Rol (UniqueID)
);

INSERT INTO Rol (nombre, descripcion) VALUES
('ADMIN', 'Administrador del sistema'),
('ASESOR', 'Asesor'),
('CLIENTE', 'Cliente Solicitante');