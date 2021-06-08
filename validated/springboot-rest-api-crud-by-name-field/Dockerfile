#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS MAVEN_BUILD
COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/
RUN mvn -X -f  /build/pom.xml clean package -DskipTests


#
# Package stage
#
FROM adoptopenjdk/openjdk11:x86_64-alpine-jdk-11.0.1.13-slim
VOLUME /tmp
COPY  --from=MAVEN_BUILD /build/target/gps-0.0.1-SNAPSHOT.jar  gps.jar
ENTRYPOINT ["java","-jar","gps.jar"]
