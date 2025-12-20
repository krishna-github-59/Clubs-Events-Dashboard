# Use Java 21 JDK base image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory inside container
WORKDIR /app

# Copy Maven wrapper and pom.xml first (to cache dependencies)
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Copy your source code
COPY src ./src

# Make Maven wrapper executable
RUN chmod +x mvnw

# Build the Spring Boot JAR (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# Expose port 8080
EXPOSE 8080

# Start the Spring Boot application
CMD ["java", "-jar", "target/events-dashboard-0.0.1-SNAPSHOT.jar"]
