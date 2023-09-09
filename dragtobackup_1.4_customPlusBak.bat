@echo off
Mode Con lines=8 cols=60
color F0
title Drag To Backup

if [%1] == [] (
  color e0
  echo.Empty parameter
  timeout 0 > NUL
  exit
)

set "bakname="
if ["%bakname%"] NEQ [""] (set "bakname=_%bakname%")
if ["%bakname%"] EQU [""] (set "bakname=")

cls
FOR /F %%B IN ('WMIC OS GET LocalDateTime ^| FINDSTR \.') DO @SET B=%%B
set stamp=%B:~0,4%-%B:~4,2%-%B:~6,2% %B:~8,2%%B:~10,2%%B:~12,2%.%B:~15,3%%bakname%

set "filename=%~nx1"
set "filename=%filename: - Copy=%"
set "bkdir=%~dp1.%filename% Backups"

if exist "%~1\*" (
  goto isdir
 ) else (
   goto isfile
 )

:isdir
echo.dir
set "newname=%~nx1 %stamp%"
set "newpath=%bkdir%\%newname%"
if not exist "%newpath%" (
  xcopy /e /i /h /r /y %1 "%newpath%"
) else (
  echo.Destination item already exists!
)
exit

:isfile
echo.file
set "newname=%~n1 %stamp%%~x1"
set "newname=%newname: - Copy=%.bak"
set "newpath=%bkdir%\%newname%"
if not exist "%bkdir%" (
  md "%bkdir%"
)
if not exist "%newpath%" (
  rem (move %1 "%bkdir%\%newname%") || (color CF)
  (copy /y %1 "%newpath%" && color AF) || (color CF)
  timeout 0 > NUL
) else (
  echo.Destination item already exists!
)
exit
