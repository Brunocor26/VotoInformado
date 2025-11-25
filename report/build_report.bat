@echo off
echo ==========================================
echo Compiling VotoInformado Report using Docker
echo ==========================================

REM Check if Docker is running
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Docker is not running or not installed.
    echo Please start Docker Desktop and try again.
    pause
    exit /b 1
)

echo Pulling/Using texlive image (this may take a while first time)...

REM Clean previous build artifacts
echo Cleaning previous build artifacts...
docker run --rm -v "%cd%":/workdir -w /workdir texlive/texlive latexmk -C

REM Build the report
echo Building report...
docker run --rm -v "%cd%":/workdir -w /workdir texlive/texlive latexmk -r Latexmk -pdf -file-line-error -halt-on-error -interaction=nonstopmode -g Thesis.tex

if %errorlevel% equ 0 (
    echo.
    echo ==========================================
    echo Build Successful! Generated Thesis.pdf
    echo ==========================================
) else (
    echo.
    echo ==========================================
    echo Build Failed. Check the logs above.
    echo ==========================================
)
pause
