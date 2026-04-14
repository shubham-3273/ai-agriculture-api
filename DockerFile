# --- Stage 1: Build the Java Project ---
# We use a Maven image with Java 17 to build the app
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy your pom.xml and download the internet dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy your actual code and your AI model into the container
COPY src ./src
COPY AI_model ./AI_model

# Build the final Spring Boot application
RUN mvn clean package -DskipTests

# --- Stage 2: Run the Application ---
# We switch to a lighter Java image just for running the app
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the finished app from Stage 1
COPY --from=build /app/target/*.jar app.jar
# Copy the AI model folder from Stage 1 so the app can find it
COPY --from=build /app/AI_model ./AI_model

# Open port 8080 for the internet
EXPOSE 8080

# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]