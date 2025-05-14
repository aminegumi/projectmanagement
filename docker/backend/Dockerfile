# Build stage for Backend
FROM maven:3.8.4-openjdk-17 as backend-build
WORKDIR /projectmanagement/backend

# Copy the entire backend folder to ensure all necessary files are included
COPY backend/ ./

# Build the application
RUN mvn clean package -DskipTests

# Final stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /projectmanagement
COPY --from=backend-build /projectmanagement/backend/target/*.jar app.jar

# Expose port
EXPOSE 8082

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]