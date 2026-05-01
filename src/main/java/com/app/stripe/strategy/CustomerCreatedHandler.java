package com.app.stripe.strategy;

import com.app.stripe.logging.EventLogger;
import com.stripe.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerCreatedHandler implements EventHandler {
    private final EventLogger logger;

    @Override
    public void handle(Event event) {
        logger.logMessage("New customer registered");
    }

    @Override
    public String getEventType() {
        return "customer.created";
    }
}
