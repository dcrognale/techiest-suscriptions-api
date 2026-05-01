package com.app.stripe.router;

import com.app.stripe.strategy.EventHandler;
import com.stripe.model.Event;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventStrategyRouter {

    private final List<EventHandler> handlers;
    private Map<String, EventHandler> handlerMap;

    @PostConstruct
    public void init() {
        handlerMap = handlers.stream()
                .collect(Collectors.toMap(EventHandler::getEventType, Function.identity()));
    }

    public void route(String eventType, Event event) {
        log.info("Handling route event: {}", eventType);
        EventHandler handler = handlerMap.get(eventType);
        if ( Objects.nonNull(handler)) {
            handler.handle(event);
        } else {
            log.info("No handler found for event type: {}", eventType);
        }
    }
}
