# Recommended API File Structure

## Standard Layered Architecture

For a REST API, the recommended structure follows a **layered architecture** pattern:

```
src/main/java/com/transittracker/
├── TransitTrackerApplication.java    # Main Spring Boot app (if using Spring)
│
├── controller/                        # REST Controllers (API endpoints)
│   ├── TransitController.java
│   ├── RouteController.java
│   └── StationController.java
│
├── service/                           # Business Logic Layer
│   ├── TransitService.java
│   ├── RouteService.java
│   └── StationService.java
│
├── repository/                        # Data Access Layer
│   ├── TransitRepository.java
│   ├── RouteRepository.java
│   └── StationRepository.java
│
├── model/                             # Domain Models / Entities
│   ├── Transit.java
│   ├── Route.java
│   └── Station.java
│
├── dto/                               # Data Transfer Objects (API requests/responses)
│   ├── request/
│   │   ├── CreateTransitRequest.java
│   │   └── UpdateRouteRequest.java
│   └── response/
│       ├── TransitResponse.java
│       └── RouteResponse.java
│
├── exception/                         # Custom Exception Handling
│   ├── ResourceNotFoundException.java
│   ├── ValidationException.java
│   └── GlobalExceptionHandler.java
│
├── config/                            # Configuration Classes
│   ├── DatabaseConfig.java
│   ├── SecurityConfig.java
│   └── WebConfig.java
│
└── util/                              # Utility Classes
    ├── DateUtils.java
    └── ValidationUtils.java
```

## Layer Responsibilities

### 1. **Controller Layer** (`controller/`)
- **Purpose**: Handle HTTP requests and responses
- **Responsibilities**:
  - Receive HTTP requests
  - Validate input (basic validation)
  - Call service layer
  - Return HTTP responses
  - Handle status codes (200, 404, 500, etc.)

**Example:**
```java
@RestController
@RequestMapping("/api/transit")
public class TransitController {
    private final TransitService transitService;
    
    @GetMapping("/{id}")
    public ResponseEntity<TransitResponse> getTransit(@PathVariable Long id) {
        // Handle GET request
    }
    
    @PostMapping
    public ResponseEntity<TransitResponse> createTransit(@RequestBody CreateTransitRequest request) {
        // Handle POST request
    }
}
```

### 2. **Service Layer** (`service/`)
- **Purpose**: Business logic and orchestration
- **Responsibilities**:
  - Implement business rules
  - Coordinate between multiple repositories
  - Transform between entities and DTOs
  - Handle transactions
  - Validate business rules

**Example:**
```java
@Service
public class TransitService {
    private final TransitRepository transitRepository;
    
    public TransitResponse createTransit(CreateTransitRequest request) {
        // Business logic here
        // Validate business rules
        // Transform DTO to Entity
        // Save to repository
        // Return response DTO
    }
}
```

### 3. **Repository Layer** (`repository/`)
- **Purpose**: Data access and persistence
- **Responsibilities**:
  - CRUD operations
  - Database queries
  - Data persistence
  - No business logic here!

**Example:**
```java
@Repository
public interface TransitRepository {
    Transit findById(Long id);
    Transit save(Transit transit);
    List<Transit> findAll();
    void delete(Long id);
}
```

### 4. **Model/Entity Layer** (`model/`)
- **Purpose**: Domain models representing database entities
- **Responsibilities**:
  - Represent database tables
  - Define relationships
  - JPA/Hibernate annotations

**Example:**
```java
@Entity
@Table(name = "transit")
public class Transit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private LocalDateTime departureTime;
    // Getters and setters
}
```

### 5. **DTO Layer** (`dto/`)
- **Purpose**: Data Transfer Objects for API communication
- **Responsibilities**:
  - Separate API contract from internal models
  - Request DTOs: What the API receives
  - Response DTOs: What the API returns
  - Validation annotations

**Example:**
```java
public class CreateTransitRequest {
    @NotBlank
    private String name;
    
    @NotNull
    private LocalDateTime departureTime;
    // Getters and setters
}
```

### 6. **Exception Layer** (`exception/`)
- **Purpose**: Custom exception handling
- **Responsibilities**:
  - Custom exceptions
  - Global exception handler
  - Error response formatting

### 7. **Config Layer** (`config/`)
- **Purpose**: Application configuration
- **Responsibilities**:
  - Database configuration
  - Security settings
  - CORS configuration
  - Bean definitions

### 8. **Util Layer** (`util/`)
- **Purpose**: Reusable utility functions
- **Responsibilities**:
  - Helper methods
  - Common utilities
  - Static helper classes

## Resources Directory

```
src/main/resources/
├── application.properties          # or application.yml
├── application-dev.properties
├── application-prod.properties
└── db/
    └── migration/                  # Database migrations (if using Flyway/Liquibase)
        └── V1__initial_schema.sql
```

## Test Directory Structure

```
src/test/java/com/transittracker/
├── controller/
│   └── TransitControllerTest.java
├── service/
│   └── TransitServiceTest.java
├── repository/
│   └── TransitRepositoryTest.java
└── integration/
    └── TransitApiIntegrationTest.java
```

## Benefits of This Structure

1. **Separation of Concerns**: Each layer has a single responsibility
2. **Testability**: Easy to test each layer independently
3. **Maintainability**: Changes in one layer don't affect others
4. **Scalability**: Easy to add new features
5. **Industry Standard**: Follows common Java/Spring Boot patterns

## Data Flow

```
HTTP Request
    ↓
Controller (validates input, maps to DTO)
    ↓
Service (business logic, transforms DTO ↔ Entity)
    ↓
Repository (database operations)
    ↓
Database
    ↓
Repository (returns Entity)
    ↓
Service (transforms Entity → DTO)
    ↓
Controller (returns HTTP Response)
    ↓
HTTP Response
```

## Alternative: Feature-Based Structure

Some teams prefer organizing by feature instead of by layer:

```
src/main/java/com/transittracker/
├── transit/
│   ├── TransitController.java
│   ├── TransitService.java
│   ├── TransitRepository.java
│   ├── Transit.java
│   └── TransitDto.java
├── route/
│   ├── RouteController.java
│   ├── RouteService.java
│   └── ...
└── shared/
    ├── exception/
    ├── config/
    └── util/
```

Both approaches are valid - choose based on your team's preference!

