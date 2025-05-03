# Task Management API

## Project Setup and Running Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- MySQL (for production) / H2 (for development)

### Setup Steps
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

## Development Assumptions

1. Authentication:
   - Users must register before accessing any task-related endpoints
   - JWT tokens are used for session management
   - Tokens expire after 24 hours

2. Database:
   - H2 in-memory database for development environment
   - MySQL for production environment
   - Database schema is automatically created on startup

3. Task Management:
   - Tasks belong to a single user
   - Tasks can have multiple categories
   - Tasks have a completion status (completed/not completed)

## Technologies and Libraries Used

### Core Technologies
- Spring Boot 3.4.5
- Java 17
- Maven

### Security
- Spring Security
- JWT (JSON Web Tokens)
- BCrypt password encryption

### Database
- Spring Data JPA
- H2 Database (Development)
- MySQL Connector (Production)

### Testing
- JUnit 5
- Spring Boot Test
- AssertJ
- Spring Security Test

### Development Tools
- Lombok
- Spring Boot DevTools
- Spring Validation

## Challenges Faced and Solutions

1. **Authentication Implementation**
   - Challenge: Implementing secure JWT-based authentication
   - Solution: Used Spring Security with JWT filter chain and custom UserDetailsService

2. **Exception Handling**
   - Challenge: Consistent error responses across the application
   - Solution: Implemented global exception handling with @ControllerAdvice

## API Specification

### Authentication Endpoints

#### Register User
```
POST /api/auth/signup
Content-Type: application/json

Request:
{
    "username": "string",
    "email": "string",
    "password": "string"
}

Response:
{
    "id": "long",
    "username": "string",
    "email": "string"
}
```

#### Login
```
POST /api/auth/signin
Content-Type: application/json

Request:
{
    "username": "string",
    "password": "string"
}

Response:
{
    "token": "string",
    "type": "Bearer"
}
```

### Task Endpoints

#### Create Task
```
POST /api/tasks
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
    "title": "string",
    "description": "string",
    "categories": ["string"]
}

Response:
{
    "id": "long",
    "title": "string",
    "description": "string",
    "completed": "boolean",
    "categories": ["string"],
    "createdAt": "datetime",
    "updatedAt": "datetime"
}
```

#### Get Tasks
```
GET /api/tasks
Authorization: Bearer {token}

Query Parameters:
- completed (optional): boolean
- category (optional): string
- search (optional): string

Response:
[
    {
        "id": "long",
        "title": "string",
        "description": "string",
        "completed": "boolean",
        "categories": ["string"],
        "createdAt": "datetime",
        "updatedAt": "datetime"
    }
]
```

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
