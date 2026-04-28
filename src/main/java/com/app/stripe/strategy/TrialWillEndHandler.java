package com.app.stripe.strategy;

import com.app.stripe.logging.EventLogger;
import com.stripe.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrialWillEndHandler implements EventHandler {
    private final EventLogger logger;

    @Override
    public void handle(Event event) {
        logger.logEventStart(event);
        logger.logMessage("Trial ending soon — customer notified");
    }

    @Override
    public String getEventType() {
        return "customer.subscription.trial_will_end";
    }
}
