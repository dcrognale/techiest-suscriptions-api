package com.app.stripe.store;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class IdempotencyStore {
    private final ConcurrentHashMap<String, Boolean> processedEvents = new ConcurrentHashMap<>();

    public boolean isProcessed(String eventId) {
        return processedEvents.containsKey(eventId);
    }

    public void markProcessed(String eventId) {
        processedEvents.put(eventId, Boolean.TRUE);
    }
}
