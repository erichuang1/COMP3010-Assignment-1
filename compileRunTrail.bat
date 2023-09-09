@echo off
:restart
javac ass1_comp3010.java -d "%~dp0bin"
java -cp bin ass1_comp3010<"%~dp0doc\Trial3\test10.6.txt"
pause
cls
goto:restart