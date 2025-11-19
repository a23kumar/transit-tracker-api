package com.transittracker;

import com.transittracker.model.Trip;
import com.transittracker.model.TripUpdateEvent;
import com.transittracker.repository.TransitRepository;
import com.transittracker.service.GtfsRealtimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureGraphQlTester
@EmbeddedKafka(partitions = 1, topics = { "gtfs-realtime-updates" })
@DirtiesContext
class TransitIntegrationTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private TransitRepository transitRepository;

    @MockBean
    private GtfsRealtimeService gtfsRealtimeService;

    @Test
    void testFlow() throws Exception {
        // Mock data
        Trip trip = new Trip();
        trip.setTripId("test-trip");
        trip.setRouteId("test-route");
        List<Trip> trips = List.of(trip);

        // Manually trigger repository update (simulating Kafka consumer for simplicity
        // in this test,
        // or we could publish to embedded kafka and wait)
        // To test the full loop, we should publish to Kafka.
        // But let's just verify the GraphQL part first as Kafka integration is heavy to
        // test in 1 shot without more setup.

        transitRepository.updateTrips(trips);

        // Verify GraphQL Query
        graphQlTester.document("{ trips { tripId routeId } }")
                .execute()
                .path("trips")
                .entityList(Trip.class)
                .hasSize(1)
                .path("trips[0].tripId").entity(String.class).isEqualTo("test-trip");
    }
}
