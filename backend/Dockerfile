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
COPY conf/config.json $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $VERTICLE_FILE"]