# Personal Task Manager

A personal task manager application built with clean architecture principles, featuring REST API endpoints, background reminder processing with multithreading, and dynamic task sorting.

## Features

-  Create, read, update, and delete tasks
-  Automated reminder system using scheduled thread pools
-  Dynamic sorting by priority, due date, or category
-  Thread-safe operations for concurrent access
- ️ Clean architecture with clear separation of concerns
-  Input validation and comprehensive error handling
-  Lombok for reduced boilerplate code

## Technologies

- **Java 21**
- **Spring Boot 2.7.14**
- **Lombok 1.18.28** - Reduces boilerplate code
- **Maven** - Dependency management
- **JUnit 5** - Testing
- **SLF4J** - Logging

## Architecture

The application follows Clean Architecture principles with four main layers:

```
┌─────────────────────────────────────────┐
│      Presentation Layer (REST API)      │
├─────────────────────────────────────────┤
│     Application Layer (DTOs/Mappers)    │
├─────────────────────────────────────────┤
│  Infrastructure Layer (Implementations)  │
├─────────────────────────────────────────┤
│    Domain Layer (Entities/Use Cases)    │
└─────────────────────────────────────────┘
```

### Domain Layer
- **Entities**: Task, Priority, Category
- **Use Cases**: CreateTask, GetTasks, UpdateTask, DeleteTask
- **Repository Interfaces**: TaskRepository

### Application Layer
- **DTOs**: Request/Response objects (using Lombok)
- **Mappers**: Entity to DTO conversions

### Infrastructure Layer
- **Repository Implementation**: In-memory task storage with thread-safety
- **Services**: ReminderService (multithreading), TaskSortingService

### Presentation Layer
- **Controllers**: REST API endpoints (using Lombok)
- **Exception Handlers**: Global error handling

## Prerequisites

- Java 11 or higher
- Maven 3.6+
- Lombok plugin for your IDE:
    - **IntelliJ IDEA**: Enable annotation processing (Settings → Build → Compiler → Annotation Processors)
    - **Eclipse**: Install Lombok from https://projectlombok.org/setup/eclipse
    - **VS Code**: Install "Lombok Annotations Support" extension

## Installation

1. Clone the repository:
```bash
git clone https://github.com/Paul0116/personal-task-manager.git
cd personal-task-manager
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

## Lombok Annotations Used

- **@Data**: Generates getters, setters, toString, equals, and hashCode
- **@Getter**: Generates getter methods
- **@Builder**: Implements the Builder pattern
- **@RequiredArgsConstructor**: Generates constructor for final fields
- **@AllArgsConstructor**: Generates constructor with all fields
- **@NoArgsConstructor**: Generates no-args constructor
- **@Slf4j**: Generates SLF4J logger

## API Endpoints

### Create Task
```http
POST /api/tasks
Headers: X-User-Id: {userId}
Content-Type: application/json

{
  "title": "Complete project report",
  "priority": 4,
  "dueDate": "2025-11-15T10:00:00",
  "category": "WORK"
}
```

### Get Tasks
```http
GET /api/tasks?sortBy=PRIORITY&startDate=2025-11-01T00:00:00&endDate=2025-11-30T23:59:59
Headers: X-User-Id: {userId}
```

**Query Parameters:**
- `sortBy` (optional): PRIORITY, DUE_DATE, CATEGORY, CREATED_AT (default: CREATED_AT)
- `startDate` (optional): Filter tasks from this date
- `endDate` (optional): Filter tasks until this date

### Get Task by ID
```http
GET /api/tasks/{id}
Headers: X-User-Id: {userId}
```

### Update Task
```http
PUT /api/tasks/{id}
Headers: X-User-Id: {userId}
Content-Type: application/json

{
  "title": "Updated title",
  "priority": 5,
  "dueDate": "2025-11-16T15:00:00",
  "category": "PERSONAL"
}
```

### Delete Task
```http
DELETE /api/tasks/{id}
Headers: X-User-Id: {userId}
```

## Task Fields

- **title** (required): String, max 200 characters
- **priority** (required): Integer, 1-5 (1=LOWEST, 5=HIGHEST)
- **dueDate** (required): ISO 8601 DateTime format
- **category** (required): WORK, PERSONAL, SHOPPING, HEALTH, EDUCATION, FINANCE, OTHER

## Reminder Service

The application includes a background reminder service that:
- Checks for due tasks every 60 seconds (configurable)
- Uses a thread pool for concurrent processing
- Ensures thread-safe updates with locking mechanisms
- Logs reminders when tasks reach their due date

### Configuration

Edit `application.properties`:
```properties
reminder.thread-pool-size=5
reminder.check-interval-seconds=60
```

## Testing

Run unit tests:
```bash
mvn test
```

Run integration tests:
```bash
mvn verify
```

## Thread Safety

The application ensures thread-safe operations through:
- **ConcurrentHashMap** for task storage
- **Synchronized methods** for critical sections
- **ReentrantLock** for reminder processing
- **Thread pools** for concurrent reminder checks

## Example Usage

```bash
# Create a task
curl -X POST http://localhost:8080/api/tasks \
  -H "X-User-Id: user123" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Buy groceries",
    "priority": 3,
    "dueDate": "2025-11-15T18:00:00",
    "category": "SHOPPING"
  }'

# Get all tasks sorted by priority
curl -X GET "http://localhost:8080/api/tasks?sortBy=PRIORITY" \
  -H "X-User-Id: user123"

# Update a task
curl -X PUT http://localhost:8080/api/tasks/{taskId} \
  -H "X-User-Id: user123" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Buy groceries and cook dinner",
    "priority": 4
  }'

# Delete a task
curl -X DELETE http://localhost:8080/api/tasks/{taskId} \
  -H "X-User-Id: user123"
```

## Project Structure

```
personal-task-manager/
├── src/main/java/com/taskmanager/
│   ├── domain/
│   │   ├── entity/Task.java
│   │   ├── valueobject/
│   │   │   ├── Priority.java
│   │   │   └── Category.java
│   │   ├── repository/TaskRepository.java
│   │   └── usecase/
│   │       ├── CreateTaskUseCase.java
│   │       ├── GetTasksUseCase.java
│   │       ├── UpdateTaskUseCase.java
│   │       └── DeleteTaskUseCase.java
│   ├── application/
│   │   ├── dto/
│   │   │   ├── CreateTaskRequest.java
│   │   │   ├── UpdateTaskRequest.java
│   │   │   ├── TaskResponse.java
│   │   │   └── TaskListResponse.java
│   │   └── mapper/TaskMapper.java
│   ├── infrastructure/
│   │   ├── persistence/InMemoryTaskRepository.java
│   │   ├── service/
│   │   │   ├── ReminderService.java
│   │   │   └── TaskSortingService.java
│   │   └── config/ApplicationConfig.java
│   ├── presentation/
│   │   ├── controller/TaskController.java
│   │   └── exception/
│   │       ├── GlobalExceptionHandler.java
│   │       └── ErrorResponse.java
│   └── TaskManagerApplication.java
└── src/test/java/
```

## Author

**Paul Christian Argao**

## Acknowledgments

- Clean Architecture principles by Robert C. Martin
- Spring Boot framework
- Project Lombok for reducing boilerplate code
