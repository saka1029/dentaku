@echo off
setlocal
set DIR=%USERPROFILE%/git/dentaku/target
set JAR=%DIR%/dentaku-1.0-jar-with-dependencies.jar
java -jar %JAR%
endlocal