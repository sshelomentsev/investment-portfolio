FROM arangodb/arangodb

ENV ARANGO_ROOT_PASSWORD=admin

COPY init-database.js /docker-entrypoint-initdb.d/init-database.js
