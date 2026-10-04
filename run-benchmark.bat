@echo off
setlocal
chcp 65001 > nul
cd /d "%~dp0"

echo ===================================================
echo   FastEnvironment JMH Benchmark Suite
echo   Win32 FFM vs Standard Java Locale/Runtime
echo ===================================================
echo.

echo [1/2] Building FastEnvironment locally...
call mvn clean package -DskipTests -q
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] FastEnvironment build failed!
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Packaging JMH uber-jar...
cd examples\Benchmark
call mvn clean package -DskipTests -q
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] JMH benchmark packaging failed!
    cd ..\..
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo Running JMH Benchmarks...
java --enable-preview --enable-native-access=ALL-UNNAMED -jar target\benchmarks.jar

cd ..\..
pause
