#!/bin/bash
set pname=sync-server-1.0-SNAPSHOT.jar
PID=$(ps -e|grep pname|awk '{printf $1}')
if [ $? -eq 0 ]; then
    echo "process id:$PID"
else
    echo "process pname not exit"
    exit
fi
kill -9 ${PID}
if [ $? -eq 0 ];then
    echo "kill pname success"
else
    echo "kill pname fail"
fi