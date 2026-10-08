@echo off
echo ===================================================
echo Compiling Pet Adoption & Veterinary System...
echo ===================================================

if not exist bin mkdir bin

javac -d bin -sourcepath src src/com/petcare/Main.java src/com/petcare/model/*.java src/com/petcare/service/*.java src/com/petcare/exception/*.java src/com/petcare/ui/*.java src/com/petcare/util/*.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed.
    pause
    exit /b %ERRORLEVEL%
)

echo [SUCCESS] Compilation successful!
echo Starting application...
echo ===================================================
java -cp bin com.petcare.Main
pause
