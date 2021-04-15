FROM adoptopenjdk/openjdk11:alpine-slim as build

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
RUN ./mvnw dependency:go-offline

COPY src src
RUN ./mvnw package -DskipTests

FROM adoptopenjdk/openjdk11:alpine-jre
ARG JAR_FILE=target/*.jar
COPY --from=build ${JAR_FILE} spring-in-docker.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "spring-in-docker.jar"]
