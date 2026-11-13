@echo off
title Paint ENSA Safi
cd /d "%~dp0"
echo ========================================================
echo        Lancement de l'application Paint ENSA Safi
echo        Universite Cadi Ayyad - ENSA Safi
echo ========================================================

set "JAVA_BIN=java"
if exist "C:\Program Files\Java\jdk-26\bin\javaw.exe" (
    set "JAVA_BIN=C:\Program Files\Java\jdk-26\bin\javaw.exe"
)

start "" "%JAVA_BIN%" -jar "%~dp0PaintJava.jar"
if %ERRORLEVEL% NEQ 0 (
    echo [Info] Tentative de lancement avec java standard...
    java -jar "%~dp0PaintJava.jar"
    if %ERRORLEVEL% NEQ 0 (
        echo [ERREUR] Impossible de lancer le fichier Java.
        pause
    )
)
