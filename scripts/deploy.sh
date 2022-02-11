#!/usr/bin/env bash

mvn clean package

echo 'Copy files...'

scp target/Messenger3.0-0.0.1-SNAPSHOT.jar\
    root@178.208.92.181:~/messenger3/

echo 'Restart server...'

ssh root@178.208.92.181 << EOF

pgrep java | xargs kill -9
nohup java -jar ~/messenger3/Messenger3.0-0.0.1-SNAPSHOT.jar > ~/messenger3/log.txt &

EOF

echo 'Bye'