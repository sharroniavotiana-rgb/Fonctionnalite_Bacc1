
CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    contact VARCHAR(255) NOT NULL
);


CREATE TABLE status (
    id BIGSERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL
);


CREATE TABLE types_devis (
    id BIGSERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL
);


CREATE TABLE demandes (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    lieu VARCHAR(255) NOT NULL,
    district VARCHAR(255) NOT NULL,
    client_id BIGINT NOT NULL,
    CONSTRAINT fk_demandes_client FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE RESTRICT
);


CREATE TABLE devis (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    montant_total DECIMAL(15, 2) DEFAULT 0.00,
    type_devis_id BIGINT NOT NULL,
    demande_id BIGINT,
    CONSTRAINT fk_devis_type FOREIGN KEY (type_devis_id) REFERENCES types_devis(id) ON DELETE RESTRICT,
    CONSTRAINT fk_devis_demande FOREIGN KEY (demande_id) REFERENCES demandes(id) ON DELETE SET NULL
);


CREATE TABLE details_devis (
    id BIGSERIAL PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL,
    montant DECIMAL(15, 2) NOT NULL,
    devis_id BIGINT NOT NULL,
    CONSTRAINT fk_details_devis FOREIGN KEY (devis_id) REFERENCES devis(id) ON DELETE CASCADE
);


CREATE TABLE demande_status (
    id BIGSERIAL PRIMARY KEY,
    date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    commentaire TEXT,
    demande_id BIGINT NOT NULL,
    status_id BIGINT NOT NULL,
    CONSTRAINT fk_demande_status_demande FOREIGN KEY (demande_id) REFERENCES demandes(id) ON DELETE CASCADE,
    CONSTRAINT fk_demande_status_status FOREIGN KEY (status_id) REFERENCES status(id) ON DELETE RESTRICT
);


CREATE INDEX idx_demandes_client_id ON demandes(client_id);
CREATE INDEX idx_devis_type_devis_id ON devis(type_devis_id);
CREATE INDEX idx_devis_demande_id ON devis(demande_id);
CREATE INDEX idx_details_devis_devis_id ON details_devis(devis_id);
CREATE INDEX idx_demande_status_demande_id ON demande_status(demande_id);
CREATE INDEX idx_demande_status_status_id ON demande_status(status_id);
CREATE INDEX idx_demande_status_date ON demande_status(date);


-- Clients
INSERT INTO clients (nom, contact) VALUES
('Société Minier Rova', '+261 34 12 345 67'),
('BTP Construct', '+261 33 98 765 43'),
('Cabinet Géo Mada', '+261 38 55 111 22');

-- Status
INSERT INTO status (libelle) VALUES
('En attente'),
('En cours'),
('Validé'),
('Rejeté');

-- Types de devis
INSERT INTO types_devis (libelle) VALUES
('Etude'),
('Etude'),
('Forage');

-- Demandes
INSERT INTO demandes (date, lieu, district, client_id) VALUES
('2024-01-10', 'Ambatondrazaka', 'Alaotra Mangoro', 1),
('2024-02-05', 'Moramanga', 'Alaotra Mangoro', 2),
('2024-03-15', 'Antananarivo', 'Analamanga', 3);

-- Devis
INSERT INTO devis (date, montant_total, type_devis_id, demande_id) VALUES
('2024-01-15', 0.00, 1, 1),
('2024-02-10', 0.00, 1, 2),
('2024-03-20', 0.00, 2, 3);

-- Détails devis
INSERT INTO details_devis (libelle, montant, devis_id) VALUES
('Forage 50m',        3500000.00, 1),
('Analyse du sol',     800000.00, 1),
('Forage 80m',        5200000.00, 2),
('Pompe immergée',    1500000.00, 2),
('Rapport géologique', 950000.00, 3),
('Déplacement terrain', 300000.00, 3);

-- Mise à jour des montants totaux
UPDATE devis SET montant_total = (
    SELECT COALESCE(SUM(montant), 0)
    FROM details_devis
    WHERE details_devis.devis_id = devis.id
);

-- Demande status
INSERT INTO demande_status (date, commentaire, demande_id, status_id) VALUES
('2024-01-10 08:00:00', 'Demande reçue',              1, 1),
('2024-01-20 10:30:00', 'Dossier en cours d analyse', 1, 2),
('2024-02-05 09:00:00', 'Demande reçue',              2, 1),
('2024-02-15 14:00:00', 'Devis validé par le client', 2, 3),
('2024-03-15 11:00:00', 'Demande reçue',              3, 1);