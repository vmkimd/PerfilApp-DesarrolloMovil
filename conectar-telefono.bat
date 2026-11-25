@echo off
cd /d "%~dp0"
set ADB="%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
%ADB% devices > info_pc.txt 2>&1
%ADB% reverse tcp:3000 tcp:3000 >> info_pc.txt 2>&1
%ADB% reverse --list >> info_pc.txt 2>&1
curl.exe -s http://localhost:3000/ >> info_pc.txt 2>&1
