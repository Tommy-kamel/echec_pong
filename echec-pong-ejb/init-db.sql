-- Script d'initialisation de la base de données MySQL
-- Ce script est exécuté automatiquement au démarrage du conteneur Docker

USE echec_pong_db;

-- Création de la table game_settings
CREATE TABLE IF NOT EXISTS game_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    board_width INT NOT NULL DEFAULT 8,
    pion_health INT NOT NULL DEFAULT 3,
    cavalier_health INT NOT NULL DEFAULT 5,
    fou_health INT NOT NULL DEFAULT 5,
    tour_health INT NOT NULL DEFAULT 5,
    dame_health INT NOT NULL DEFAULT 8,
    roi_health INT NOT NULL DEFAULT 10,
    first_serve VARCHAR(10) NOT NULL DEFAULT 'black',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insertion des paramètres par défaut
INSERT INTO game_settings (name, board_width, pion_health, cavalier_health, fou_health, tour_health, dame_health, roi_health, first_serve)
VALUES 
    ('default', 8, 3, 5, 5, 5, 8, 10, 'black'),
    ('easy', 8, 5, 7, 7, 7, 10, 15, 'black'),
    ('hard', 8, 2, 3, 3, 3, 5, 7, 'black'),
    ('small_board', 4, 3, 5, 5, 5, 8, 10, 'white'),
    ('large_board', 8, 3, 5, 5, 5, 8, 10, 'black')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Afficher les données insérées
SELECT * FROM game_settings;
