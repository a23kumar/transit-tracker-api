# Transit Tracker API

A real-time GraphQL API for tracking transit information, built with Spring Boot and Kafka.

## Features

*   **GraphQL API**: Query for trips, vehicles, and stop times.
*   **Real-time Updates**: Subscribe to live trip updates via GraphQL Subscriptions (WebSockets).
*   **Kafka Integration**: Publishes and consumes GTFS Realtime data using Kafka.
*   **Automatic Polling**: Fetches data from the GTFS Realtime feed every 30 seconds.
*   **Filtering**: Filter trips by `routeId` and `vehicleId`.

## Prerequisites

*   Java 21 or higher
*   Docker & Docker Compose (for Kafka)

## Getting Started

### 1. Start Infrastructure
Start the Kafka and Zookeeper containers:

```bash
docker-compose up -d # If you do not want to see logs
docker-compose up # If you want to see logs
```

### 2. Run the Application
Start the Spring Boot server:

```bash
./gradlew bootRun
```

The server will start on `http://localhost:8080`.

### 3. Access the API
Open the custom GraphiQL interface in your browser:

**[http://localhost:8080/graphiql.html](http://localhost:8080/graphiql.html)**

## API Usage

### Queries

**Get all trips:**
```graphql
query {
  trips {
    tripId
    routeId
    vehicle {
      id
      label
    }
  }
}
```

**Filter trips by Route and Vehicle:**
```graphql
query {
  trips(routeId: "301", vehicleId: "21501") {
    tripId
    routeId
    vehicle {
      label
    }
  }
}
```

### Subscriptions

**Subscribe to all updates:**
```graphql
subscription {
  feedUpdates {
    timestamp
    trips {
      tripId
      stopTimeUpdates {
        stopSequence
        arrival {
          delay
        }
      }
    }
  }
}
```

**Subscribe to updates for a specific route:**
```graphql
subscription {
  feedUpdates(routeId: "301") {
    timestamp
    trips {
      tripId
      vehicle {
        label
      }
    }
  }
}
```

## Development

To run with hot reloading (automatically restarts on file changes):

```bash
./gradlew bootRun --continuous
```
