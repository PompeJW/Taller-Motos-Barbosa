# Etapa 1: Compilar el proyecto con Gradle
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# Etapa 2: Imagen final con solo el JAR
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
