package com.app.stripe.persistence.store;

import com.app.stripe.persistence.entity.ProcessedEvent;
import com.app.stripe.persistence.repository.ProcessedEventRepository;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdempotencyStore {

    private final ProcessedEventRepository repository;

    public boolean isProcessed(String eventId) {
        return repository.existsByEventId(eventId);
    }

    public void markProcessed(String eventId) {
        repository.saveAndFlush(new ProcessedEvent(eventId));
    }
}
