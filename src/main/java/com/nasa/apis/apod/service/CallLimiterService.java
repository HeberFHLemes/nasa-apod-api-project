package com.nasa.apis.apod.service;

import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Component
public class CallLimiterService {

    private static final String COUNTER_FILE = "./data/count.txt";
    private static final String RESET_FILE = "./data/last-reset.txt";
    private static final int MAX_CALLS = 30;

    private int callCount;

    private LocalDateTime lastReset;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // Control the max number of calls.
    public CallLimiterService() {
        ensureFilesExist();
        this.lastReset = loadLastReset();
        checkReset();
        this.callCount = loadCallCount();
    }

    public synchronized boolean canCall() {
        checkReset();
        if (callCount >= MAX_CALLS) return false;
        callCount++;
        saveCallCount();
        return true;
    }

    private void checkReset() {
        LocalDateTime now = LocalDateTime.now();
        if (lastReset == null || Duration.between(lastReset, now).toHours() >= 24) {
            resetCounter();
            lastReset = now;
            saveLastReset();
        }
    }

    private void resetCounter() {
        callCount = 0;
        saveCallCount();
    }

    private int loadCallCount() {
        try {
            Path path = Paths.get(COUNTER_FILE);
            if (Files.exists(path)) {
                String content = Files.readString(path).trim();
                return Integer.parseInt(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private LocalDateTime loadLastReset() {
        try {
            Path path = Paths.get(RESET_FILE);
            if (Files.exists(path)) {
                String content = Files.readString(path).trim();

                if (!content.isBlank()){
                    return LocalDateTime.parse(content, formatter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveCallCount() {
        try {
            Files.writeString(Paths.get(COUNTER_FILE), String.valueOf(callCount));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveLastReset() {
        try {
            Files.writeString(Paths.get(RESET_FILE), lastReset.format(formatter));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ensureFilesExist() {
        try {
            Path counterPath = Paths.get(COUNTER_FILE);
            Path resetPath = Paths.get(RESET_FILE);

            Path dataDir = counterPath.getParent();
            if (dataDir != null && !Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }

            if (!Files.exists(counterPath)) {
                Files.writeString(counterPath, "0");
            }

            if (!Files.exists(resetPath) || Files.readString(resetPath).isBlank()) {
                Files.writeString(resetPath, LocalDateTime.now().format(formatter));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
