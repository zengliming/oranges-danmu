@echo off
chcp 936
rem 获取进程相关信息
for /f "delims=" %%i in ('jps ^|findstr sync-server-1.0-SNAPSHOT.jar') do (
set str=%%i
rem 替换字符获取pid
set "pid=%str:sync-server-1.0-SNAPSHOT.jar= %"
rem 根据pid kill进程
call taskkill /F /pid %pid%
cmd.exe
)