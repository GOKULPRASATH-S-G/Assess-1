@echo off
echo ===================================================
echo Building Pet Adoption & Veterinary System (Maven)...
echo ===================================================

call mvn clean package -DskipTests=true

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven build failed.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo [SUCCESS] Build successful!
echo Starting application...
echo ===================================================
java -jar target\pet-adoption-veterinary-system-1.0.0.jar
pause
