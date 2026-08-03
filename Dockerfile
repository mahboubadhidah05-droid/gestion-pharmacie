# Étape 1 : construction du .jar à partir du code source
# (garantit un jar toujours frais et correct, peu importe où le build tourne)
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Étape 2 : image finale, légère, juste pour exécuter le jar construit
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]