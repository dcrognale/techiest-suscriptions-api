package com.app.stripe.logging;

import com.stripe.model.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventLogger {

    public void logEventStart(Event event) {
        log.info("eventId=\"{}\" eventType=\"{}\" timestamp=\"{}\" payload={}", 
                 event.getId(), event.getType(), event.getCreated(), event.toJson());
    }

    public void logMessage(String message) {
        log.info(message);
    }
}
