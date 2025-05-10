# Build stage for Backend
FROM maven:3.8.4-openjdk-17 as backend-build
WORKDIR /projectmanagement/backend
COPY backend/pom.xml ./
RUN mvn dependency:go-offline
COPY backend/src ./src/
RUN mvn clean package -DskipTests

# Final stage
FROM openjdk:17-jdk-slim
WORKDIR /projectmanagement
COPY --from=backend-build /projectmanagement/backend/target/*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]