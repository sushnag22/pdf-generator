# First stage: Build the application with Gradle 8.10.2 and JDK 21
FROM gradle:8.10.2-jdk21 AS build
WORKDIR /app

# Copy the Gradle project files
COPY . .

# Build the project and create the JAR files
RUN ./gradlew build --no-daemon --stacktrace

# Second stage: Create the final image with JDK 21
FROM eclipse-temurin:21
WORKDIR /app

# Copy the normal JAR file (not the "plain" one) from the build stage
COPY --from=build /app/build/libs/*-SNAPSHOT.jar /app/application.jar

# Expose the port the Spring Boot application will run on
EXPOSE 8080

# Command to run the Spring Boot application
CMD ["java", "-jar", "application.jar"]
