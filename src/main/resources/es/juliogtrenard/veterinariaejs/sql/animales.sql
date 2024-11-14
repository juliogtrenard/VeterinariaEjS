DROP SCHEMA IF EXISTS `animales`;
CREATE SCHEMA `animales`;
USE `animales`;

CREATE TABLE `Animales` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  `especie` varchar(100) DEFAULT NULL,
  `raza` varchar(100) DEFAULT NULL,
  `sexo` varchar(100) DEFAULT NULL,
  `edad` int(11) DEFAULT NULL,
  `peso` int(11) DEFAULT NULL,
  `observaciones` varchar(100) DEFAULT NULL,
  `fecha_primera_consulta` date DEFAULT NULL,
  `foto` blob DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;