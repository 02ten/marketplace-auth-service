FROM gradle:8.6 AS TEMP_BUILD
ENV DIR=/app
WORKDIR $DIR
COPY . $DIR
USER root
RUN gradle bootJar
RUN echo "Build Completed!"

FROM amazoncorretto:20
ENV DIR=/app
ENV JAR_NAME=Auth_Service-0.0.1-SNAPSHOT.jar
ENV DB_PORT=5432
WORKDIR $DIR
COPY --from=TEMP_BUILD $DIR/build/libs/$JAR_NAME .
EXPOSE 8900
ENTRYPOINT java -jar $JAR_NAME