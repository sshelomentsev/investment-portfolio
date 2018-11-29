#!/bin/bash

mvn clean package -f backend/pom.xml

docker-compose up --build --force-recreate
