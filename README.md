# Transit Tracker API

A Java-based API for tracking transit information.

## Prerequisites

- Java 21 or higher
- Gradle 7.0 or higher (or use the Gradle Wrapper)

## Building the Project

To compile the project:

```bash
./gradlew build
```

Or if you have Gradle installed globally:

```bash
gradle build
```

To run the application:

```bash
./gradlew run
```

Or:

```bash
gradle run
```

To build a JAR:

```bash
./gradlew build
```

The JAR will be located at `build/libs/transit-tracker-api-1.0.0-SNAPSHOT.jar`

## Running Tests

```bash
./gradlew test
```

Or:

```bash
gradle test
```

## Project Structure

```
transit-tracker-api/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── transittracker/
│   │               └── App.java
│   └── test/
│       └── java/
│           └── com/
│               └── transittracker/
│                   └── AppTest.java
├── build.gradle
├── settings.gradle
└── README.md
```

## Development

Start developing by modifying the `App.java` file in `src/main/java/com/transittracker/`.


