@echo off
chcp 936
set dir=%cd%
set logOutDir=%dir%\logs
set logPath=%dir%\config\log4j2.xml
call cd lib
rem 获取当前路径
set dir=%cd%
rem 凭借命令
set cmds=java -Xms800m -Xmx800m -XX:MaxNewSize=512m -jar
set port=9000
set filename=danmu-server-1.0-SNAPSHOT.jar
set result=%cmds% %dir%\%filename% port=%port% logPath=%logPath% logOutDir=%logOutDir%
call %result%
cmd.exe