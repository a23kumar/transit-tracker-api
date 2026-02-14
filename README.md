# Transit Tracker API

A real-time GraphQL API for tracking Grand River Transit (GRT) buses, built with **Kotlin**, **Spring Boot**, and enriched with static GTFS data.

## Features

*   **GraphQL API**: Query trips, vehicle positions, routes, and stops
*   **Enriched Data**: Route names, stop names, and trip headsigns joined from static GTFS data — not just raw IDs
*   **Real-time Updates**: Subscribe to live trip updates via GraphQL Subscriptions (WebSockets)
*   **Vehicle Tracking**: Live vehicle positions with lat/lon, bearing, and speed
*   **Automatic Polling**: Fetches data from GTFS Realtime feeds every 30 seconds
*   **Static GTFS Loader**: Downloads and loads GRT's `google_transit.zip` on startup into an embedded H2 database
*   **Zero Infrastructure**: No Kafka, no Docker — just run the JAR

## Prerequisites

*   Java 21 or higher

## Getting Started

### 1. Run the Application

```bash
./gradlew bootRun
```

The server will start on `http://localhost:8080`. On startup, it will:
1. Download and parse GRT's static GTFS data (routes, stops, trips)
2. Begin polling realtime trip updates and vehicle positions every 30 seconds

### 2. Access the API

Open the GraphiQL interface in your browser:

**[http://localhost:8080/graphiql.html](http://localhost:8080/graphiql.html)**

## API Usage

### Queries

**Get all trips with enriched data:**
```graphql
query {
  trips {
    tripId
    routeId
    routeName
    tripHeadsign
    vehicle {
      id
      label
    }
    stopTimeUpdates {
      stopId
      stopName
      arrival {
        delay
      }
    }
  }
}
```

**Filter trips by route:**
```graphql
query {
  trips(routeId: "7") {
    tripId
    routeName
    vehicle { label }
  }
}
```

**Get live vehicle positions:**
```graphql
query {
  vehiclePositions {
    vehicleId
    label
    latitude
    longitude
    bearing
    speed
    routeId
    routeName
  }
}
```

**Get all routes:**
```graphql
query {
  routes {
    routeId
    routeShortName
    routeLongName
  }
}
```

**Get all stops:**
```graphql
query {
  stops {
    stopId
    stopName
    stopLat
    stopLon
  }
}
```

### Subscriptions

**Subscribe to all trip updates:**
```graphql
subscription {
  feedUpdates {
    timestamp
    trips {
      tripId
      routeName
      stopTimeUpdates {
        stopName
        arrival { delay }
      }
    }
  }
}
```

**Subscribe to a specific route:**
```graphql
subscription {
  feedUpdates(routeId: "7") {
    timestamp
    trips {
      tripId
      vehicle { label }
    }
  }
}
```

## Architecture

```
GRT Static GTFS (zip) ──▶ H2 Database (routes, stops, trips)
                                 │
GRT Realtime Feeds ──▶ Polling Service ──▶ In-Memory Cache ──▶ GraphQL API
  (trip updates)           (30s)               │                   │
  (vehicle positions)                    Enrichment via H2    Queries + Subscriptions
```

## Development

Run with hot reloading:
```bash
./gradlew bootRun --continuous
```

Run tests:
```bash
./gradlew test
```

## Tech Stack

- **Language**: Kotlin
- **Framework**: Spring Boot 3.2
- **API**: GraphQL (Spring GraphQL + WebSocket subscriptions)
- **Database**: H2 (embedded, in-memory) for static GTFS data
- **Data Source**: GRT GTFS Realtime (protobuf) + Static GTFS
