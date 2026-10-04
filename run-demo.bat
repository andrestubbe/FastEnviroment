@echo off
setlocal
chcp 65001 > nul
cd /d "%~dp0"

echo ===================================================
echo   FastEnvironment Terminal Showcase Demo
echo   OS Language, Regional & Culture Telemetry
echo ===================================================
echo.

echo [1/2] Building FastEnvironment...
call mvn clean package -DskipTests -q
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Build failed!
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Building and Launching Demo...
cd examples\Demo
call mvn compile -DskipTests -q
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Demo compilation failed!
    cd ..\..
    pause
    exit /b %ERRORLEVEL%
)

java --enable-preview -cp "target\classes;..\..\target\FastEnvironment-0.1.0.jar" fastenvironment.demo.Demo

cd ..\..
pause
