# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the Maven project files
# This helps Docker cache the dependencies layer if pom.xml doesn't change
COPY pom.xml .
COPY src ./src

# Build the application
# We skip tests in the Docker build as they should be run separately in CI
RUN ./mvnw package -Dmaven.test.skip=true

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "target/spring-boot-crud-api-0.0.1-SNAPSHOT.jar"]
