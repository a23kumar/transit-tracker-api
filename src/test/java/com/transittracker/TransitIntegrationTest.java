package com.transittracker;

import com.transittracker.model.Trip;
import com.transittracker.model.Vehicle;
import com.transittracker.repository.TransitRepository;
import com.transittracker.service.DataPollingService;
import com.transittracker.service.GtfsRealtimeService;
import com.transittracker.service.GtfsStaticDataLoader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.Arrays;

@SpringBootTest
@AutoConfigureGraphQlTester
public class TransitIntegrationTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private TransitRepository transitRepository;

    // Mock these so the test doesn't actually poll external APIs
    @MockBean
    private GtfsRealtimeService gtfsRealtimeService;

    @MockBean
    private GtfsStaticDataLoader gtfsStaticDataLoader;

    @MockBean
    private DataPollingService dataPollingService;

    @Test
    public void testGraphQLTripsQueryReturnsData() {
        // Seed test data
        Trip trip = new Trip();
        trip.setTripId("test-trip-1");
        trip.setRouteId("7");
        trip.setRouteName("King");
        Vehicle vehicle = new Vehicle("101", "Bus 101");
        trip.setVehicle(vehicle);

        transitRepository.updateTrips(Arrays.asList(trip));

        // Query and verify
        graphQlTester.document("{ trips { tripId routeId routeName vehicle { id label } } }")
                .execute()
                .path("trips").entityList(Object.class).hasSize(1)
                .path("trips[0].tripId").entity(String.class).isEqualTo("test-trip-1")
                .path("trips[0].routeName").entity(String.class).isEqualTo("King");
    }

    @Test
    public void testGraphQLTripsQueryFiltersByRouteId() {
        Trip trip1 = new Trip();
        trip1.setTripId("trip-a");
        trip1.setRouteId("7");
        trip1.setRouteName("King");

        Trip trip2 = new Trip();
        trip2.setTripId("trip-b");
        trip2.setRouteId("12");
        trip2.setRouteName("Maple");

        transitRepository.updateTrips(Arrays.asList(trip1, trip2));

        graphQlTester.document("{ trips(routeId: \"7\") { tripId routeId } }")
                .execute()
                .path("trips").entityList(Object.class).hasSize(1)
                .path("trips[0].routeId").entity(String.class).isEqualTo("7");
    }

    @Test
    public void testVehiclePositionsQueryReturnsEmptyWhenNoData() {
        graphQlTester.document("{ vehiclePositions { vehicleId latitude longitude } }")
                .execute()
                .path("vehiclePositions").entityList(Object.class).hasSize(0);
    }
}
