#!/bin/bash

mvn clean package -f backend/pom.xml

#npm install
#cd webapp
#npm run ng --prod
#cd ..

docker-compose up --build --force-recreate
