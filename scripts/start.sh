#!/usr/bin/env bash

mvn clean package #-DskipTests

scp target/restaurant_advisor_react-0.0.1-SNAPSHOT.jar \
    docker/back-end/

cd docker

docker-compose build
docker-compose up