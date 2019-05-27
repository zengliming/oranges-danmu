@echo off
chcp 936
call cd ../
call java -Xms800m -Xmx800m -XX:MaxNewSize=512m -jar sync-server-1.0-SNAPSHOT.jar port=8088
cmd.exe