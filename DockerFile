# Run Stage
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage to the run stage
ARG JAR_FILE
COPY ${JAR_FILE} FindYourPet-backend.jar

# Set environment variable to specify Spring profile
ENV SPRING_PROFILES_ACTIVE=prod

# Expose the application port
EXPOSE 8088

# Run the application
ENTRYPOINT ["java", "-jar", "/app/FindYourPet-backend.jar"]