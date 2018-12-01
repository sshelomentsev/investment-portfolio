#!/bin/bash

mvn clean package -f backend/pom.xml

cd webapp
ng build --prod
cd ..

docker-compose up --build --force-recreate
