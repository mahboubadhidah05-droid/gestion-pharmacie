@echo off
title Gestion Pharmacie - Serveur
cd /d "%~dp0"

echo ============================================
echo   Demarrage de Gestion Pharmacie
echo   (Tableau de bord + Medicaments + Ventes +
echo    Clients + Commandes)
echo ============================================
echo.

set DB_PASSWORD=douaa
set DB_URL=jdbc:mysql://localhost:3307/pharmacie
set SERVER_PORT=9091

if not exist "target\pharmacie-corrige-0.0.1-SNAPSHOT.jar" (
    echo Le fichier .jar n'existe pas encore, compilation en cours...
    call mvn clean package -DskipTests
    echo.
)

echo Ouverture du navigateur dans 5 secondes...
start "" cmd /c "timeout /t 5 >nul & start http://localhost:9091/login.html"

echo Lancement du serveur ^(laisse cette fenetre ouverte^) :
echo Apres connexion, tu arriveras sur le nouveau tableau de bord.
echo.
java -jar target\pharmacie-corrige-0.0.1-SNAPSHOT.jar

echo.
echo Le serveur s'est arrete. Appuie sur une touche pour fermer.
pause >nul
