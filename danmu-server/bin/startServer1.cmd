@echo off
chcp 936
call cd ../
call cd target
call java -jar danmu-server-1.0-SNAPSHOT-jar-with-dependencies.jar port=8089
cmd.exe