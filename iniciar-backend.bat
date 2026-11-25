@echo off
title Backend PerfilApp
cd /d "%~dp0backend"
call npm install
npm start
pause
