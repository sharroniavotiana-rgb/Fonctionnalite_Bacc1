CREATE TABLE candidat (
    id_candidat BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100),
    prenom VARCHAR(100) NOT NULL UNIQUE
); 

CREATE TABLE matiere (
    id_matiere BIGSERIAL PRIMARY KEY,
    matiere VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE correcteur (
    id_correcteur BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100),
    prenom VARCHAR(100) NOT NULL UNIQUE
); 

CREATE TABLE resolution (
    id_resolution BIGSERIAL PRIMARY KEY,
    resolution VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE operateur (
    id_operateur BIGSERIAL PRIMARY KEY,
    operateur VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE parametre (
    id_parametre BIGSERIAL PRIMARY KEY,
    id_matiere BIGINT NOT NULL,
    id_operateur BIGINT NOT NULL,
    difference DOUBLE PRECISION NOT NULL,
    id_resolution BIGINT NOT NULL,
    FOREIGN KEY (id_matiere) REFERENCES matiere(id_matiere),
    FOREIGN KEY (id_operateur) REFERENCES operateur(id_operateur),
    FOREIGN KEY (id_resolution) REFERENCES resolution(id_resolution)
);

CREATE TABLE note (
    id_note BIGSERIAL PRIMARY KEY,
    id_candidat BIGINT NOT NULL,
    id_matiere BIGINT NOT NULL,
    id_correcteur BIGINT NOT NULL,
    note DOUBLE PRECISION NOT ,
    
    FOREIGN KEY (id_candidat) REFERENCES candidat(id_candidat),
    FOREIGN KEY (id_matiere) REFERENCES matiere(id_matiere),
    FOREIGN KEY (id_correcteur) REFERENCES correcteur(id_correcteur)
);

_ les resolutions sont: plus petit, plus grand et moyenne 
- les operateurs sont: < , >, >=, <= 
- le petit projet fonctionne comme suit:
- pour une matiere d un candidat, il peut y avoir plusieurs correcteurs donc plusieurs notes attribuer pour chaque correcteur
- la note finale prise se joue dans la table parametre c a d par exemple  
    pour la matiere anglais si < (operateur) que 2 (difference) on prend le plus grand note (resolution)
- la difference c est la difference des notes notes donnees par les correcteur: par exemple le correcteur1 donne 14, et le correcteur 2 donne 12 donc la difference est 2
- Si il y a plus de 2 correcteurs on prend la somme des differences comme difference c a d: 
par exemple le correcteur1 donne 14, le correcteur2 donne 12, le correcteur3 donne 11 donc la difference est 6
- avec les note 14, 12, 11 en fait la difference de 14 -12 = 2, puis de 14 - 11 = 3, 12 -11 = 1, 2 + 3+ 1 = 6
- Les opérateurs dans la table parametre peut etre plusieurs ligne par matiere
- Les resolutions sont les 3 que j ai site

je veux les crud des 2 tables, note et parametre dans l afficahe 
et je veux aussi un champ ou je peux entrer differentes notes pour une matiere et quand je clique sur un bouton 
"note finale" on me donne le note finale prise par rapport au regle 
Les differentes notes que j entre pour la matiere est separer par point virgule 
Avez vous compris avant que nous passons au codage 