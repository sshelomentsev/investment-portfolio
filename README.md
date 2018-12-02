# Investment portfolio
A test application to manage investment portfolio.

## Dependencies 
1) Java 8 + maven
2) Angular 7 + npm
4) ArangoDB
3) Docker

## How to run
1) Build backend application
  * ```cd backend```
  * ```mvn clean package```
2) Build frontend application
  * ```cd webapp```
  * ```npm install```
  * ```npm run ng build --prod```
3) Build docker images
  * ```docker-compose up```
4) Open http://localhost:3000/ in your browser.  

### If you need to run it locally, then you need
1) Install ArangoDB and initialize database by using database/Dockerfile or manual installation and executing an init script database/init-database.sh (Please refer to https://docs.arangodb.com/3.0/Manual/Administration/Arangosh/ in case of how to run shell script in ArangoDB).
2) Specify database settings into backend config (backend/conf/config)
3) Build and run backend by
  * ```cd backend```
  * ```mvn clean package```
  * ```java -jar target/ investment-portfolio-fat.jar```
4) Build and run frontend by
  * ```cd webapp```
  * ```npm install```
  * ```ng serve```
  
 
