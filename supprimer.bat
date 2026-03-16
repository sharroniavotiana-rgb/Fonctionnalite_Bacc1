@echo off
echo ============================================
echo NETTOYAGE DE LA BASE DE DONNEES
echo ============================================
echo.

REM Configuration - A MODIFIER SELON TES PARAMETRES
set PGHOST=localhost
set PGPORT=5432
set PGUSER=postgres
set PGDATABASE=Bacc

REM Demander le mot de passe (ou le definir en variable d'env)
set /p PGPASSWORD="Mot de passe PostgreSQL : "

echo.
echo Suppression des donnees en cours...

REM Execution des commandes SQL
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %PGDATABASE% -c "DELETE FROM note;"
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %PGDATABASE% -c "DELETE FROM parametre;"
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %PGDATABASE% -c "DELETE FROM correcteur;"
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %PGDATABASE% -c "DELETE FROM candidat;"
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %PGDATABASE% -c "DELETE FROM matiere;"
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %PGDATABASE% -c "DELETE FROM operateur;"
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %PGDATABASE% -c "DELETE FROM resolution;"

echo.
echo Reinitialisation des sequences...

psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %PGDATABASE% -c "ALTER SEQUENCE note_id_note_seq RESTART WITH 1;"
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %PGDATABASE% -c "ALTER SEQUENCE parametre_id_parametre_seq RESTART WITH 1;"
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %PGDATABASE% -c "ALTER SEQUENCE correcteur_id_correcteur_seq RESTART WITH 1;"
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %PGDATABASE% -c "ALTER SEQUENCE candidat_id_candidat_seq RESTART WITH 1;"
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %PGDATABASE% -c "ALTER SEQUENCE matiere_id_matiere_seq RESTART WITH 1;"
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %PGDATABASE% -c "ALTER SEQUENCE operateur_id_operateur_seq RESTART WITH 1;"
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %PGDATABASE% -c "ALTER SEQUENCE resolution_id_resolution_seq RESTART WITH 1;"

echo.
echo Verification...

psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %PGDATABASE% -c "SELECT 'note' AS table_name, COUNT(*) FROM note UNION ALL SELECT 'parametre', COUNT(*) FROM parametre UNION ALL SELECT 'correcteur', COUNT(*) FROM correcteur UNION ALL SELECT 'candidat', COUNT(*) FROM candidat UNION ALL SELECT 'matiere', COUNT(*) FROM matiere UNION ALL SELECT 'operateur', COUNT(*) FROM operateur UNION ALL SELECT 'resolution', COUNT(*) FROM resolution ORDER BY table_name;"

echo.
echo ============================================
echo NETTOYAGE TERMINE !
echo ============================================
pause