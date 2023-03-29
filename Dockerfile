## Build stage

FROM maven:3.8.6-eclipse-temurin-17-alpine AS build

WORKDIR /build
COPY pom.xml ./pom.xml
COPY src/ src/
RUN mvn clean verify

## Runtime stage

FROM eclipse-temurin:17-jre-alpine
LABEL maintainer=""

COPY --from=build /build/target/officeroulette.jar /officeroulette.jar

# FIXME transition into using entrypoint script when higher startup
# customization is needed
ENTRYPOINT [ "java" ]
CMD [ "-jar", "officeroulette.jar"]
