# Script PowerShell de compilation et construction du JAR
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "       Compilation et Generation de Paint ENSA         " -ForegroundColor Cyan
Write-Host "       Universite Cadi Ayyad - ENSA Safi               " -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan

$baseDir = $PSScriptRoot
Set-Location $baseDir

# Trouver jar.exe
$jarCmd = "jar"
if (-not (Get-Command jar -ErrorAction SilentlyContinue)) {
    $jdk = Get-ChildItem "C:\Program Files\Java\jdk*" -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($jdk -and (Test-Path "$($jdk.FullName)\bin\jar.exe")) {
        $jarCmd = "$($jdk.FullName)\bin\jar.exe"
    }
}

Write-Host "[1/4] Nettoyage du dossier bin..." -ForegroundColor Yellow
if (Test-Path "bin") { Remove-Item "bin" -Recurse -Force }
New-Item -ItemType Directory -Path "bin" | Out-Null

Write-Host "[2/4] Compilation des codes sources Java..." -ForegroundColor Yellow
$javaFiles = Get-ChildItem -Recurse "src\main\java\*.java" | Select-Object -ExpandProperty FullName
& javac -encoding UTF-8 -d bin $javaFiles
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERREUR] La compilation a echoue." -ForegroundColor Red
    exit 1
}

Write-Host "[3/4] Copie des ressources..." -ForegroundColor Yellow
Copy-Item -Path "src\main\resources\*" -Destination "bin" -Recurse -Force

Write-Host "[4/4] Creation de PaintJava.jar..." -ForegroundColor Yellow
& $jarCmd cfm "PaintJava.jar" Manifest.txt -C bin .
Copy-Item "PaintJava.jar" "Paint-ENSA-Safi.jar" -Force

Write-Host "========================================================" -ForegroundColor Green
Write-Host "   [SUCCES] Executable genere : PaintJava.jar           " -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Green
