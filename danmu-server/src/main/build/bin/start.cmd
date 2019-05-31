@echo off
chcp 936
call cd ../
rem 获取当前路径
set dir=%cd%
rem 凭借命令
set cmds=java -Xms800m -Xmx800m -XX:MaxNewSize=512m -jar
set port=9000
set filename=sync-server-1.0-SNAPSHOT.jar
set result=%cmds% %dir%\%filename% %port%
call %result%
cmd.exe