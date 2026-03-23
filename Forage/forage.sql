
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