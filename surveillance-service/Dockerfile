FROM maven:3.9-eclipse-temurin-21 as build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

#-----------------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --form=build /app/target/surveillance-service-*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
