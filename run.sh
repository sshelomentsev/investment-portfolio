#!/bin/bash

mvn clean package -f backend/pom.xml

docker-compose build
docker-compose up
