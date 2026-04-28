package com.app.stripe.middleware;

import com.app.stripe.store.IdempotencyStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class IdempotencyGuard {
    
    private final IdempotencyStore store;

    public boolean checkAndSet(String eventId) {
        if (store.isProcessed(eventId)) {
            log.info("Duplicate event skipped: [{}]", eventId);
            return true;
        }
        return false;
    }
    
    public void markCompleted(String eventId) {
        store.markProcessed(eventId);
    }
}
