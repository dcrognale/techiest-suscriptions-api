package com.app.stripe.strategy;

import com.stripe.model.Event;

public interface EventHandler {
    void handle(Event event);
    String getEventType();
}
