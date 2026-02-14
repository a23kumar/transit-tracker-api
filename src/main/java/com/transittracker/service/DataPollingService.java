package com.transittracker.service;

import com.transittracker.repository.TransitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DataPollingService {

    private static final Logger logger = LoggerFactory.getLogger(DataPollingService.class);

    private final GtfsRealtimeService gtfsRealtimeService;
    private final TransitRepository transitRepository;

    public DataPollingService(GtfsRealtimeService gtfsRealtimeService, TransitRepository transitRepository) {
        this.gtfsRealtimeService = gtfsRealtimeService;
        this.transitRepository = transitRepository;
    }

    @Scheduled(fixedRate = 30_000, initialDelay = 5_000)
    public void pollGtfsData() {
        logger.info("Polling GTFS Realtime data...");

        // Poll trip updates
        try {
            var trips = gtfsRealtimeService.fetchTripUpdates();
            transitRepository.updateTrips(trips);
            logger.info("Fetched and stored {} trip updates", trips.size());
        } catch (Exception e) {
            logger.error("Error fetching trip updates", e);
        }

        // Poll vehicle positions
        try {
            var positions = gtfsRealtimeService.fetchVehiclePositions();
            transitRepository.updateVehiclePositions(positions);
            logger.info("Fetched and stored {} vehicle positions", positions.size());
        } catch (Exception e) {
            logger.error("Error fetching vehicle positions", e);
        }
    }
}
