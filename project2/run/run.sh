#!/bin/bash

# file that gets run when the docker container starts up
# Sam Fryer.

#first, start the nodejs server to receive any initial commands
node run/run.js $1 &

# sleep 5

# if [ $1 -eq 1 ]
# then
# 	curl "http://localhost:8080/?block=172.18.0.23&indrop=0.2"
# fi

# if [ $1 -eq 2 ]
# then
# 	curl "http://localhost:8080/?indrop=0.2"
# fi

# if [ $1 -eq 3 ]
# then
# 	curl "http://localhost:8080/?block=172.18.0.21&indrop=0.2"
# fi

#then wait 10 seconds to make sure the commands came and executed
sleep 10

#finally, start the initial program
java Main $1 $2 $3

