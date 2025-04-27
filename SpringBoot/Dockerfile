# ---------- Stage 1: Build ----------
FROM openjdk:21-jdk as build

WORKDIR /app

COPY .mvn .mvn
COPY src src
COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .

RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests=true

# ---------- Stage 2: Run ----------
FROM openjdk:21-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
