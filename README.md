# TaskFlow - Project Management Application

TaskFlow is a comprehensive project management application inspired by Jira, built with React and Spring Boot.

## Features

- User Authentication & Authorization
- Project Management
- Sprint Planning
- Task Management with Drag & Drop
- Real-time Updates
- Role-based Access Control

## Prerequisites

- Node.js 16+
- Java 17+
- Docker & Docker Compose
- Maven

## Local Development Setup

1. Clone the repository:
```bash
git clone https://github.com/yourusername/taskflow.git
cd taskflow
```

2. Install dependencies:
```bash
# Install frontend dependencies
cd frontend
npm install
cd ..

# Install backend dependencies
cd backend
mvn install
```

3. Start the development servers:

```bash
# Start backend server
cd backend
mvn spring-boot:run

# In another terminal, start frontend server
cd frontend
npm run dev
```

The application will be available at:
- Frontend: http://localhost:3000
- Backend: http://localhost:8082

## Running with Docker

1. Build and run the application using Docker Compose:

```bash
docker-compose up --build
```

2. Access the application at http://localhost:3000

## Testing

```bash
# Run frontend tests
cd frontend
npm test

# Run backend tests
cd backend
mvn test
```

## Monitoring

The application includes monitoring setup with:
- Prometheus for metrics collection
- Grafana for metrics visualization
- ELK Stack for log aggregation

Access monitoring tools:
- Grafana: http://localhost:3000/grafana
- Prometheus: http://localhost:9090
- Kibana: http://localhost:5601

## CI/CD Pipeline

The project uses GitHub Actions for CI/CD:
- Automated testing on pull requests
- Automated builds and deployments
- Code quality checks
- Security scanning

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
