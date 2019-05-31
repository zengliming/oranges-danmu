#!/bin/sh
cd `dirname $0`
cd ../
path=`pwd`
nohup java -Xms800m -Xmx800m -XX:MaxNewSize=512m -jar ${path}/sync-server-1.0-SNAPSHOT.jar port=9090 >>/dev/null &