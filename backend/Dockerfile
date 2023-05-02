# Stage 1: Maven Build
FROM maven:3.9.1-eclipse-temurin-17-alpine as builder

# Set working directory
WORKDIR /app

# Copy pom.xml and source code to the container
COPY pom.xml .

# Download dependencies only if pom changed
RUN mvn dependency:go-offline

# Copy the source code
COPY src ./src

# Run Maven build
RUN mvn -B -e clean package -DskipTests

# Stage 2: Build final image
FROM eclipse-temurin:20-jdk

# Set working directory
WORKDIR /app

# Copy built JAR file from the builder stage
COPY --from=builder /app/target/*.jar ./app.jar

# Expose application port
EXPOSE 8080

# Start the application
CMD ["java", "-jar", "app.jar"]
