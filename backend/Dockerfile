FROM java:8-jre

ENV VERTICLE_FILE investment-portfolio-fat.jar

ENV VERTICLE_HOME /usr/verticles

EXPOSE 8888

# Copy your fat jar to the container
COPY target/$VERTICLE_FILE $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $VERTICLE_FILE"]