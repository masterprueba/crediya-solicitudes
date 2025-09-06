FROM eclipse-temurin:17.0.16_8-jdk-alpine
EXPOSE 8080
WORKDIR /app
COPY applications/app-service/build/libs/solicitudes.jar /app/solicitudes.jar
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
RUN adduser -D appuser
USER appuser
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -jar solicitudes.jar" ]
