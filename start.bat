@echo off
start "Stock Tracker - Backend" cmd /k "cd /d "%~dp0backend" && set ALPHA_VANTAGE_KEY=demo&& "%USERPROFILE%\maven\apache-maven-3.9.6\bin\mvn.cmd" spring-boot:run"
start "Stock Tracker - Frontend" cmd /k "cd /d "%~dp0frontend" && npm start"
timeout /t 6 /nobreak >nul
start http://localhost:4200
