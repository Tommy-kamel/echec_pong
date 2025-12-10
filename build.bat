@echo off
echo Creation du JAR et du ZIP...
mvn clean package
echo.
echo Creation du runtime avec jlink...
mvn javafx:jlink
echo.
echo ========================================
echo Fichiers crees dans target/ :
echo - echec_pong-1.0-SNAPSHOT.jar (JAR fat)
echo - echec_pong.zip (Runtime complet)
echo ========================================
pause
