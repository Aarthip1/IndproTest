# Task Management API

A Spring Boot application for managing tasks with user authentication and categorization features.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- MySQL (for production) / H2 (for development)

## Quick Start

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd indproTest
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

## Features

- User authentication with JWT
- Task management (create, read, update, delete)
- Task categorization
- H2 in-memory database for development
- RESTful API endpoints

## API Endpoints

### Authentication
- POST `/api/auth/signup` - Register a new user
- POST `/api/auth/signin` - Login and get JWT token

### Tasks
- GET `/api/tasks` - Get all tasks for authenticated user
- POST `/api/tasks` - Create a new task
- PUT `/api/tasks/{id}` - Update a task
- DELETE `/api/tasks/{id}` - Delete a task

### Categories
- GET `/api/categories` - Get all categories
- POST `/api/categories` - Create a new category

## Development

### Database
The application uses H2 in-memory database for development. Access the H2 console at:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:taskmanager`
- Username: `sa`
- Password: ` ` (empty)

### Testing
Run the tests using:
```bash
mvn test
```

## Configuration

Key application properties (in `src/main/resources/application.properties`):
- Server port: 8080
- H2 Console: Enabled
- JWT expiration: 24 hours
- Logging level: DEBUG for application, INFO for Spring

## Security

- JWT-based authentication
- Passwords are encrypted using BCrypt
- H2 console is secured with Spring Security
- CSRF protection is enabled for production
