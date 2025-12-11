@echo off
REM Script pour configurer la datasource MySQL dans WildFly 28
REM Exécuter ce script avec WildFly démarré

SET WILDFLY_HOME=C:\wildfly-28.0.0.Final
SET MYSQL_CONNECTOR_PATH=%~dp0mysql-connector-j-8.0.33.jar

echo =========================================
echo Configuration WildFly pour MySQL
echo =========================================

REM 1. Télécharger le connecteur MySQL si nécessaire
if not exist "%MYSQL_CONNECTOR_PATH%" (
    echo Téléchargement du connecteur MySQL...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar' -OutFile '%MYSQL_CONNECTOR_PATH%'"
)

echo.
echo 2. Ajout du module MySQL à WildFly...
mkdir "%WILDFLY_HOME%\modules\system\layers\base\com\mysql\main" 2>nul
copy "%MYSQL_CONNECTOR_PATH%" "%WILDFLY_HOME%\modules\system\layers\base\com\mysql\main\"

REM Créer le fichier module.xml
echo ^<?xml version="1.0" encoding="UTF-8"?^> > "%WILDFLY_HOME%\modules\system\layers\base\com\mysql\main\module.xml"
echo ^<module xmlns="urn:jboss:module:1.5" name="com.mysql"^> >> "%WILDFLY_HOME%\modules\system\layers\base\com\mysql\main\module.xml"
echo     ^<resources^> >> "%WILDFLY_HOME%\modules\system\layers\base\com\mysql\main\module.xml"
echo         ^<resource-root path="mysql-connector-j-8.0.33.jar"/^> >> "%WILDFLY_HOME%\modules\system\layers\base\com\mysql\main\module.xml"
echo     ^</resources^> >> "%WILDFLY_HOME%\modules\system\layers\base\com\mysql\main\module.xml"
echo     ^<dependencies^> >> "%WILDFLY_HOME%\modules\system\layers\base\com\mysql\main\module.xml"
echo         ^<module name="javax.api"/^> >> "%WILDFLY_HOME%\modules\system\layers\base\com\mysql\main\module.xml"
echo         ^<module name="javax.transaction.api"/^> >> "%WILDFLY_HOME%\modules\system\layers\base\com\mysql\main\module.xml"
echo     ^</dependencies^> >> "%WILDFLY_HOME%\modules\system\layers\base\com\mysql\main\module.xml"
echo ^</module^> >> "%WILDFLY_HOME%\modules\system\layers\base\com\mysql\main\module.xml"

echo.
echo 3. Configuration de la datasource via CLI...
echo.
echo Assurez-vous que WildFly est démarré puis exécutez ces commandes dans jboss-cli:
echo.
echo %WILDFLY_HOME%\bin\jboss-cli.bat --connect
echo.
echo Puis entrez ces commandes:
echo -----------------------------------------------
echo /subsystem=datasources/jdbc-driver=mysql:add(driver-name=mysql,driver-module-name=com.mysql,driver-class-name=com.mysql.cj.jdbc.Driver)
echo.
echo /subsystem=datasources/data-source=EchecPongDS:add(jndi-name=java:jboss/datasources/EchecPongDS,driver-name=mysql,connection-url=jdbc:mysql://localhost:3306/echec_pong_db,user-name=echec_user,password=echec_password,min-pool-size=5,max-pool-size=20)
echo.
echo :reload
echo -----------------------------------------------
echo.
echo 4. Ensuite déployez l'application:
echo    copy target\echec-pong-ejb.war %WILDFLY_HOME%\standalone\deployments\
echo.
pause
