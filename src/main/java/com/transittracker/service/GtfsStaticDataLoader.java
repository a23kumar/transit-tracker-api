package com.transittracker.service;

import com.transittracker.config.GtfsConfig;
import com.transittracker.entity.GtfsRoute;
import com.transittracker.entity.GtfsStop;
import com.transittracker.entity.GtfsTrip;
import com.transittracker.repository.GtfsRouteRepository;
import com.transittracker.repository.GtfsStopRepository;
import com.transittracker.repository.GtfsTripRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class GtfsStaticDataLoader implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(GtfsStaticDataLoader.class);

    private final GtfsConfig gtfsConfig;
    private final HttpClient httpClient;
    private final GtfsRouteRepository routeRepository;
    private final GtfsStopRepository stopRepository;
    private final GtfsTripRepository tripRepository;
    private final GtfsRealtimeService gtfsRealtimeService;

    public GtfsStaticDataLoader(GtfsConfig gtfsConfig, HttpClient httpClient,
            GtfsRouteRepository routeRepository,
            GtfsStopRepository stopRepository,
            GtfsTripRepository tripRepository,
            GtfsRealtimeService gtfsRealtimeService) {
        this.gtfsConfig = gtfsConfig;
        this.httpClient = httpClient;
        this.routeRepository = routeRepository;
        this.stopRepository = stopRepository;
        this.tripRepository = tripRepository;
        this.gtfsRealtimeService = gtfsRealtimeService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Loading static GTFS data from {} feed(s)...", gtfsConfig.getStaticGtfsUrls().size());

        for (String feedUrl : gtfsConfig.getStaticGtfsUrls()) {
            Path tempDir = null;
            try {
                byte[] zipData = downloadGtfsZip(feedUrl);
                tempDir = Files.createTempDirectory("gtfs");

                extractZip(zipData, tempDir);

                // The zip may extract files directly or into a subdirectory (e.g. "GTFS/")
                Path dataDir = findGtfsDataDir(tempDir);

                loadRoutes(dataDir.resolve("routes.txt"));
                loadStops(dataDir.resolve("stops.txt"));
                loadTrips(dataDir.resolve("trips.txt"));

                logger.info("Loaded feed: {}", feedUrl);
            } catch (Exception e) {
                logger.warn("Failed to load GTFS feed: {}. Skipping.", feedUrl, e);
            } finally {
                if (tempDir != null) {
                    deleteTempDirectory(tempDir);
                }
            }
        }

        // Refresh the realtime service caches once after all feeds are loaded
        gtfsRealtimeService.refreshCaches();

        logger.info("Static GTFS data loading complete");
    }

    private byte[] downloadGtfsZip(String url) throws IOException, InterruptedException {
        logger.info("Downloading GTFS zip from {}", url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to download GTFS zip. Status code: " + response.statusCode());
        }

        logger.info("Downloaded {} bytes", response.body().length);
        return response.body();
    }

    private void extractZip(byte[] zipData, Path destDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipData))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path filePath = destDir.resolve(entry.getName());
                if (!entry.isDirectory()) {
                    Files.createDirectories(filePath.getParent());
                    Files.copy(zis, filePath);
                }
                zis.closeEntry();
            }
        }
        logger.info("Extracted GTFS files to {}", destDir);
    }

    /**
     * Finds the directory containing routes.txt — it may be at the root or inside a
     * subdirectory.
     */
    private Path findGtfsDataDir(Path tempDir) throws IOException {
        // Check if routes.txt is at the root
        if (Files.exists(tempDir.resolve("routes.txt"))) {
            return tempDir;
        }

        // Search one level deep for a subdirectory containing routes.txt
        try (var stream = Files.list(tempDir)) {
            return stream
                    .filter(Files::isDirectory)
                    .filter(dir -> Files.exists(dir.resolve("routes.txt")))
                    .findFirst()
                    .orElseThrow(() -> new IOException("Could not find routes.txt in extracted GTFS zip"));
        }
    }

    private void loadRoutes(Path routesFile) throws IOException {
        List<GtfsRoute> routes = new ArrayList<>();
        List<String[]> rows = parseCsv(routesFile);

        if (rows.isEmpty())
            return;

        String[] headers = rows.get(0);
        int routeIdIdx = indexOf(headers, "route_id");
        int routeShortNameIdx = indexOf(headers, "route_short_name");
        int routeLongNameIdx = indexOf(headers, "route_long_name");
        int routeTypeIdx = indexOf(headers, "route_type");

        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row.length <= routeIdIdx)
                continue;

            GtfsRoute route = new GtfsRoute(
                    row[routeIdIdx],
                    routeShortNameIdx >= 0 && row.length > routeShortNameIdx ? row[routeShortNameIdx] : null,
                    routeLongNameIdx >= 0 && row.length > routeLongNameIdx ? row[routeLongNameIdx] : null,
                    routeTypeIdx >= 0 && row.length > routeTypeIdx ? parseIntOrNull(row[routeTypeIdx]) : null);
            routes.add(route);
        }

        routeRepository.saveAll(routes);
        logger.info("Loaded {} routes", routes.size());
    }

    private void loadStops(Path stopsFile) throws IOException {
        List<GtfsStop> stops = new ArrayList<>();
        List<String[]> rows = parseCsv(stopsFile);

        if (rows.isEmpty())
            return;

        String[] headers = rows.get(0);
        int stopIdIdx = indexOf(headers, "stop_id");
        int stopNameIdx = indexOf(headers, "stop_name");
        int stopLatIdx = indexOf(headers, "stop_lat");
        int stopLonIdx = indexOf(headers, "stop_lon");

        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row.length <= stopIdIdx)
                continue;

            GtfsStop stop = new GtfsStop(
                    row[stopIdIdx],
                    stopNameIdx >= 0 && row.length > stopNameIdx ? row[stopNameIdx] : null,
                    stopLatIdx >= 0 && row.length > stopLatIdx ? parseDoubleOrNull(row[stopLatIdx]) : null,
                    stopLonIdx >= 0 && row.length > stopLonIdx ? parseDoubleOrNull(row[stopLonIdx]) : null);
            stops.add(stop);
        }

        stopRepository.saveAll(stops);
        logger.info("Loaded {} stops", stops.size());
    }

    private void loadTrips(Path tripsFile) throws IOException {
        List<GtfsTrip> trips = new ArrayList<>();
        List<String[]> rows = parseCsv(tripsFile);

        if (rows.isEmpty())
            return;

        String[] headers = rows.get(0);
        int tripIdIdx = indexOf(headers, "trip_id");
        int routeIdIdx = indexOf(headers, "route_id");
        int tripHeadsignIdx = indexOf(headers, "trip_headsign");
        int directionIdIdx = indexOf(headers, "direction_id");

        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row.length <= tripIdIdx)
                continue;

            GtfsTrip trip = new GtfsTrip(
                    row[tripIdIdx],
                    routeIdIdx >= 0 && row.length > routeIdIdx ? row[routeIdIdx] : null,
                    tripHeadsignIdx >= 0 && row.length > tripHeadsignIdx ? row[tripHeadsignIdx] : null,
                    directionIdIdx >= 0 && row.length > directionIdIdx ? parseIntOrNull(row[directionIdIdx]) : null);
            trips.add(trip);
        }

        tripRepository.saveAll(trips);
        logger.info("Loaded {} trips", trips.size());
    }

    private List<String[]> parseCsv(Path file) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                rows.add(parseCsvLine(line));
            }
        }
        return rows;
    }

    /**
     * Parses a CSV line handling quoted fields (e.g. stop names containing commas).
     */
    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    // Check for escaped quote ("")
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    fields.add(current.toString());
                    current.setLength(0);
                } else {
                    current.append(c);
                }
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }

    private void deleteTempDirectory(Path dir) {
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path d, IOException exc) throws IOException {
                    Files.delete(d);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.warn("Failed to clean up temp directory: {}", dir, e);
        }
    }

    private int indexOf(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

    private Integer parseIntOrNull(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDoubleOrNull(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
