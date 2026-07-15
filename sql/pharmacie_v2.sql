/* ============================= */
/*  CREATION DE LA BASE          */
/* ============================= */
DROP DATABASE IF EXISTS pharmacie;
CREATE DATABASE pharmacie;
USE pharmacie;

/* ============================= */
/*  TABLE pharmacien             */
/* ============================= */
DROP TABLE IF EXISTS pharmacien;

CREATE TABLE pharmacien (
    id_pharmacien INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    login VARCHAR(50) UNIQUE NOT NULL,
    pwd VARCHAR(50) NOT NULL
);

/* ============================= */
/*  TABLE gestionnaire           */
/* ============================= */
DROP TABLE IF EXISTS gestionnaire;

CREATE TABLE gestionnaire (
    id_gestionnaire INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    login VARCHAR(50) UNIQUE NOT NULL,
    pwd VARCHAR(50) NOT NULL
);

/* ============================= */
/*  TABLE client                 */
/* ============================= */
DROP TABLE IF EXISTS client;

CREATE TABLE client (
    id_client INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    adresse VARCHAR(100)
);

/* ============================= */
/*  TABLE medicament             */
/* ============================= */
DROP TABLE IF EXISTS medicament;

CREATE TABLE medicament (
    id_medicament INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    dosage VARCHAR(50) NOT NULL,
    stock INT NOT NULL CHECK (stock >= 0),
    prix DOUBLE NOT NULL CHECK (prix > 0),
    seuil_critique INT DEFAULT 5
);

/* ============================= */
/*  TABLE vente                  */
/* ============================= */
DROP TABLE IF EXISTS vente;

CREATE TABLE vente (
    id_vente INT AUTO_INCREMENT PRIMARY KEY,
    id_pharmacien INT NOT NULL,
    id_client INT NOT NULL,
    id_medicament INT NOT NULL,
    quantite INT NOT NULL CHECK (quantite > 0),
    date_vente DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_pharmacien) REFERENCES pharmacien(id_pharmacien),
    FOREIGN KEY (id_client) REFERENCES client(id_client),
    FOREIGN KEY (id_medicament) REFERENCES medicament(id_medicament)
);

/* ============================= */
/*  TABLE commande               */
/* ============================= */
DROP TABLE IF EXISTS commande;

CREATE TABLE commande (
    id_commande INT AUTO_INCREMENT PRIMARY KEY,
    id_gestionnaire INT NOT NULL,
    id_medicament INT NOT NULL,
    quantite INT NOT NULL CHECK (quantite > 0),
    date_commande DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_gestionnaire) REFERENCES gestionnaire(id_gestionnaire),
    FOREIGN KEY (id_medicament) REFERENCES medicament(id_medicament)
);

/* ============================= */
/*  TABLE stock_historique       */
/* ============================= */
DROP TABLE IF EXISTS stock_historique;

CREATE TABLE stock_historique (
    id_historique INT AUTO_INCREMENT PRIMARY KEY,
    id_medicament INT NOT NULL,
    quantite INT NOT NULL,
    date_modification DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_medicament) REFERENCES medicament(id_medicament)
);

/* ============================= */
/*  DONNEES DE TEST              */
/* ============================= */

INSERT INTO pharmacien (nom, prenom, login, pwd) VALUES
('Ben Salah', 'Ali', 'pharma', '123'),
('Trablsi', 'Sami', 'pharma1','124'),
('Gabsi', 'Mahdi', 'pharma2','125');

INSERT INTO gestionnaire (nom, prenom, login, pwd) VALUES
('Slim', 'Monji', 'gest', '133'),
('Chouchen', 'Fadhel', 'gest1', '134'),
('Dhidah', 'Moncef', 'gest2', '135');

INSERT INTO client (nom, prenom, email, adresse) VALUES
('Dhawedi', 'Kacem', 'ahmed@gmail.com', 'Tunis'),
('Ajmi', 'Sami', 'sami@gmail.com', 'Tunis'),
('Ayari', 'Sara', 'sara@mail.com', 'Ariana');

INSERT INTO medicament (nom, dosage, stock, prix, seuil_critique) VALUES
('Paracetamol', '500mg', 20, 2.5, 5),
('Ibuprofene', '400mg', 50, 15, 10),
('Amoxicilline', '1g', 10, 30, 7);
INSERT INTO stock_historique (id_medicament, quantite,date_modification) VALUES
(1, 20, '2026-01-24'),
(2, 50, '2026-01-24'),
(3, 10, '2026-01-24');
