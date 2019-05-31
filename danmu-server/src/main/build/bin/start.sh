#!/bin/sh
cd `dirname $0`
cd ../
path=`pwd`
nohup java -jar ${path}/danmu-server-1.0-SNAPSHOT-jar-with-dependencies.jar port=8088 >>/dev/null &