FROM java:8-jre

ENV VERTICLE_HOME /usr/verticles

# jar
ENV VERTICLE_FILE investment-portfolio-fat.jar

# app config file
ENV VERTX_CONFIG_PATH config.json

EXPOSE 8888

# Copy jar to container
COPY target/$VERTICLE_FILE $VERTICLE_HOME/

# Copy app config file
COPY conf/config_docker.json $VERTICLE_HOME/config.json

# Copy JWT kestore
COPY keystore.jceks $VERTICLE_HOME/keystore.jceks

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.2.1/wait /wait
RUN chmod +x /wait

CMD ["/wait && exec java -jar $VERTICLE_FILE"]
