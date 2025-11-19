package com.transittracker.service;

import com.transittracker.config.KafkaConfig;
import com.transittracker.model.Trip;
import com.transittracker.model.TripUpdateEvent;
import com.transittracker.repository.TransitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataPollingService {

    private static final Logger logger = LoggerFactory.getLogger(DataPollingService.class);

    private final GtfsRealtimeService gtfsRealtimeService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TransitRepository transitRepository;

    public DataPollingService(GtfsRealtimeService gtfsRealtimeService,
            KafkaTemplate<String, Object> kafkaTemplate,
            TransitRepository transitRepository) {
        this.gtfsRealtimeService = gtfsRealtimeService;
        this.kafkaTemplate = kafkaTemplate;
        this.transitRepository = transitRepository;
    }

    @Scheduled(fixedRate = 30000)
    public void pollGtfsData() {
        logger.info("Polling GTFS Realtime data...");
        try {
            byte[] data = gtfsRealtimeService.fetchGtfsRealtimeData();
            List<Trip> trips = gtfsRealtimeService.parseGtfsRealtimeData(data);

            TripUpdateEvent event = new TripUpdateEvent(trips, System.currentTimeMillis());

            logger.info("Fetched {} trips. Publishing to Kafka topic: {}", trips.size(), KafkaConfig.TOPIC_NAME);
            kafkaTemplate.send(KafkaConfig.TOPIC_NAME, event);

        } catch (Exception e) {
            logger.error("Error fetching or publishing GTFS data", e);
        }
    }

    @KafkaListener(topics = KafkaConfig.TOPIC_NAME, groupId = "transit-tracker-group")
    public void consumeGtfsData(TripUpdateEvent event) {
        logger.info("Received update from Kafka with {} trips. Updating repository.", event.getTrips().size());
        transitRepository.updateTrips(event.getTrips());
    }
}
