# Stage 1 - BUILD
from maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

copy pom.xml .

Run mvn dependency:go-offline -B

copy src ./src

Run mvn clean package -DskipTests

# Stage 2 : runtime

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar","app.jar"]
#
#
## ---------- STAGE 1: build ----------
## We start from an image that already has Maven + a JDK installed,
## purely to compile our code. This image is large (build tools,
## compilers), but it will be THROWN AWAY after this stage - none of its
## bulk ends up in our final image.
#FROM maven:3.9-eclipse-temurin-21 AS build
#
## Sets the working directory INSIDE the image for all following
## instructions - like "cd /app" but it also creates the folder.
#WORKDIR /app
#
## We copy pom.xml FIRST, separately from the rest of the source code,
## and run dependency download as its own step. This is a deliberate
## Docker layer-caching trick: Docker caches each instruction as a
## layer. If you change a Java file but NOT pom.xml, Docker reuses the
## cached "download dependencies" layer instead of re-downloading
## everything from Maven Central on every single build. This alone can
## turn a 3-minute rebuild into a 15-second one during development.
#COPY pom.xml .
#RUN mvn dependency:go-offline -B
#
## NOW copy the actual source code (this layer changes on every code
## edit, which is fine - it's cheap, unlike the dependency download).
#COPY src ./src
#
## Compile and package into a single executable jar (the "fat jar" we
## discussed when explaining the spring-boot-maven-plugin in pom.xml).
## -DskipTests: we skip running tests during the Docker build itself -
## tests should run in CI (Phase 7, GitHub Actions) BEFORE we even
## attempt to build an image, not duplicate that work here.
#RUN mvn clean package -DskipTests
#
## ---------- STAGE 2: runtime ----------
## A MUCH smaller base image - just enough Linux + JRE (not even a full
## JDK - we don't need a compiler at runtime, only the ability to RUN
## already-compiled bytecode) to execute a Java application.
#FROM eclipse-temurin:21-jre-alpine
#
#WORKDIR /app
#
## Copy ONLY the final jar from the build stage - nothing else from
## that entire 700MB+ Maven/JDK image makes it into our final image.
## "--from=build" is what references the earlier named stage.
#COPY --from=build /app/target/order-gateway-*.jar app.jar
#
## Documents that this container listens on 8081 (matches
## application.yml). This line is informational/documentation for
## anyone reading the Dockerfile or running "docker inspect" - it does
## NOT actually publish the port; that happens in docker-compose.yml
## via the "ports:" mapping, which we'll see next.
#EXPOSE 8081
#
## The command that actually runs when a container starts from this
## image. Equivalent to running "java -jar app.jar" in a terminal.
#ENTRYPOINT ["java", "-jar", "app.jar"]