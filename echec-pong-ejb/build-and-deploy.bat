@echo off
REM Script de build et déploiement du microservice EJB

echo ========================================
echo Build et déploiement du microservice EJB
echo ========================================

REM Vérifier que WildFly est configuré
SET WILDFLY_HOME=C:\wildfly-28.0.0.Final

if not exist "%WILDFLY_HOME%\standalone" (
    echo ERREUR: WildFly non trouvé dans %WILDFLY_HOME%
    pause
    exit /b 1
)

echo.
echo 1. Compilation du projet EJB...
cd /d "%~dp0"
call ..\mvnw.cmd clean package -DskipTests

if errorlevel 1 (
    echo ERREUR: La compilation a échoué
    pause
    exit /b 1
)

echo.
echo 2. Vérification du fichier WAR...
if not exist "target\echec-pong-ejb.war" (
    echo ERREUR: Le fichier WAR n'a pas été généré
    pause
    exit /b 1
)

echo.
echo 3. Copie du WAR vers WildFly...
copy /Y "target\echec-pong-ejb.war" "%WILDFLY_HOME%\standalone\deployments\"

if errorlevel 1 (
    echo ERREUR: Impossible de copier le WAR
    pause
    exit /b 1
)

echo.
echo ========================================
echo Déploiement réussi !
echo ========================================
echo.
echo Le service sera disponible sur:
echo http://localhost:8080/echec-pong-ejb/api/settings
echo.
pause
