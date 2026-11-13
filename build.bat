@echo off
setlocal enabledelayedexpansion
title Compilation Paint ENSA Safi

echo ========================================================
echo        Compilation et Generation de Paint ENSA
echo        Universite Cadi Ayyad - ENSA Safi
echo ========================================================
echo.

cd /d "%~dp0"

:: Detection de jar.exe
set "JAR_CMD=jar"
where jar >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    if defined JAVA_HOME (
        if exist "%JAVA_HOME%\bin\jar.exe" set "JAR_CMD=%JAVA_HOME%\bin\jar.exe"
    )
    if "!JAR_CMD!"=="jar" (
        for /d %%D in ("C:\Program Files\Java\jdk*") do (
            if exist "%%D\bin\jar.exe" set "JAR_CMD=%%D\bin\jar.exe"
        )
    )
)

echo [1/4] Nettoyage et preparation du dossier de sortie...
if exist bin rd /s /q bin
mkdir bin

echo [2/4] Compilation des codes sources Java...
javac -encoding UTF-8 -d bin src\main\java\ensa\paint\*.java src\main\java\ensa\paint\model\*.java src\main\java\ensa\paint\tools\*.java src\main\java\ensa\paint\view\*.java src\main\java\ensa\paint\i18n\*.java src\main\java\ensa\paint\utils\*.java
if %ERRORLEVEL% NEQ 0 (
    echo [ERREUR] La compilation a echoue.
    pause
    exit /b %ERRORLEVEL%
)

echo [3/4] Copie des fichiers de ressources d'internationalisation...
xcopy /s /e /y "src\main\resources\*" "bin\" >nul

echo [4/4] Generation du fichier JAR executable avec : "!JAR_CMD!"...
"!JAR_CMD!" cfm PaintJava.jar Manifest.txt -C bin .
copy /y PaintJava.jar Paint-ENSA-Safi.jar >nul

echo.
echo ========================================================
echo   [SUCCES] Compilation terminee avec succes !
echo   Executable genere : PaintJava.jar
echo   Lancez l'application via : run.bat
echo ========================================================
echo.
pause
