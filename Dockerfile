# STAGE 1: Build the application JAR using Gradle
FROM gradle:8.5.0-jdk17 AS builder
# gradle:8.5.0-jdk17 is an example, use a version that matches your project's needs.
# Alternatively, you can use a base OpenJDK image and install Gradle if you prefer more control.

WORKDIR /workspace/app

# Copy only the necessary Gradle files first to leverage Docker layer caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Copy the rest of your application's source code
COPY src src

# Grant execute permissions to gradlew and run the build
# Using --no-daemon is often recommended for CI/CD environments.
# The -x test skips running tests during the Docker image build, which is common for faster CI builds.
# Remove -x test if you want tests to run as part of the image build.
RUN chmod +x ./gradlew && ./gradlew bootJar -x test --no-daemon

# STAGE 2: Create the slim runtime image
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy only the built JAR from the 'builder' stage
COPY --from=builder /workspace/app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]