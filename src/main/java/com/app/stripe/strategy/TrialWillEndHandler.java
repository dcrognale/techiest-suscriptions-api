package com.app.stripe.strategy;

import com.stripe.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrialWillEndHandler implements EventHandler {
    @Override
    public void handle(Event event) {
        log.info("Trial ending soon — customer notified");
    }

    @Override
    public String getEventType() {
        return "customer.subscription.trial_will_end";
    }
}
