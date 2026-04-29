package com.app.stripe.strategy;

import com.app.stripe.model.InvoicePayload;
import com.app.stripe.persistence.entity.AuthUser;
import com.app.stripe.persistence.repository.AuthUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoicePaymentSucceededHandler implements EventHandler {

    private final ObjectMapper objectMapper;
    private final AuthUserRepository authUserRepository;

    @Override
    public void handle(Event event) {
        log.info("{} event received", getEventType());

        try {
            String rawData = event.getData().toJson();
            InvoicePayload payload = objectMapper.readValue(rawData, InvoicePayload.class);

            if ( Objects.nonNull(payload.object())) {
                String email = payload.object().customerEmail();
                String name = payload.object().customerName();

                log.info("Processing invoice for customer: {} ({})", name, email);

                AuthUser newUser = AuthUser.builder()
                        .email(email)
                        .rawUserMetaData("{\"name\": \"" + name + "\"}")
                        .build();

                authUserRepository.save(newUser);
                log.info("User created in auth.users with email: {}", email);
            }
        } catch (Exception e) {
            log.error("Error parsing event data or saving user", e);
        }
    }

    @Override
    public String getEventType() {
        return "invoice.payment_succeeded";
    }
}
