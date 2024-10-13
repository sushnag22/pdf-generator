# Use a base image with Java installed
FROM openjdk:21-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the target folder into the container
COPY ./build/libs/pdf-generator.jar /app/application.jar

# Expose the port the Spring Boot application will run on
EXPOSE 8080

# Command to run the Spring Boot application
CMD ["java", "-jar", "application.jar"]