
DELETE FROM note;
DELETE FROM parametre;
DELETE FROM correcteur;
DELETE FROM candidat;
DELETE FROM matiere;
DELETE FROM operateur;
DELETE FROM resolution;


ALTER SEQUENCE note_id_note_seq RESTART WITH 1;
ALTER SEQUENCE parametre_id_parametre_seq RESTART WITH 1;
ALTER SEQUENCE correcteur_id_correcteur_seq RESTART WITH 1;
ALTER SEQUENCE candidat_id_candidat_seq RESTART WITH 1;
ALTER SEQUENCE matiere_id_matiere_seq RESTART WITH 1;
ALTER SEQUENCE operateur_id_operateur_seq RESTART WITH 1;
ALTER SEQUENCE resolution_id_resolution_seq RESTART WITH 1;


SELECT 'note' AS table_name, COUNT(*) FROM note
UNION ALL
SELECT 'parametre', COUNT(*) FROM parametre
UNION ALL
SELECT 'correcteur', COUNT(*) FROM correcteur
UNION ALL
SELECT 'candidat', COUNT(*) FROM candidat
UNION ALL
SELECT 'matiere', COUNT(*) FROM matiere
UNION ALL
SELECT 'operateur', COUNT(*) FROM operateur
UNION ALL
SELECT 'resolution', COUNT(*) FROM resolution;