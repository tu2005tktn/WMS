@echo off
echo Starting Warehouse Management System...
echo.

echo Checking Maven installation...
mvn --version
if %ERRORLEVEL% neq 0 (
    echo Maven is not installed or not in PATH. Please install Maven first.
    pause
    exit /b 1
)

echo.
echo Building project...
mvn clean compile

if %ERRORLEVEL% neq 0 (
    echo Build failed. Please check for errors.
    pause
    exit /b 1
)

echo.
echo Starting Tomcat server...
mvn tomcat7:run

echo.
echo Access the application at: http://localhost:8080/WMS
pause
