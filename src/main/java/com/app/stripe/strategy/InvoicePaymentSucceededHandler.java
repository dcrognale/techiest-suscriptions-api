package com.app.stripe.strategy;

import com.app.stripe.model.InvoiceData;
import com.app.stripe.model.InvoicePayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoicePaymentSucceededHandler implements EventHandler {

    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void handle(Event event) {
        log.info("{} event received", getEventType());

        try {
            String rawData = event.getData().toJson();
            InvoicePayload payload = objectMapper.readValue(rawData, InvoicePayload.class);

            if (Objects.isNull(payload.object())) {
                log.warn("Invoice payload object is null, skipping event: {}", event.getId());
                return;
            }

            InvoiceData invoice = payload.object();
            String email = invoice.customerEmail();
            String name = invoice.customerName();

            if (Objects.isNull(email) || email.isBlank()) {
                log.error("Customer email is missing in invoice {}, cannot create user", invoice.id());
                return;
            }

            log.info("Processing invoice for customer: {} ({})", name, email);

            createClientViaSupabaseFunction(email, name);

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("Error parsing invoice event JSON payload: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error handling invoice.payment_succeeded event: {}", e.getMessage(), e);
        }
    }

    private void createClientViaSupabaseFunction(String email, String displayName) {
        String sql = "SELECT public.admin_create_client_transactional(?, ?, ?, ?)";

        try {
            UUID createdUserId = jdbcTemplate.queryForObject(
                    sql,
                    UUID.class,
                    email,
                    Objects.requireNonNullElse(displayName, ""),
                    true,
                    "trainer");

            log.info("Client created successfully via admin_create_client_transactional | userId: {} | email: {}",
                    createdUserId, email);

        } catch (DataAccessException e) {
            String errorMessage = extractRootCauseMessage(e);

            if (errorMessage.contains("El email ya está registrado en el sistema de autenticación")) {
                log.warn("Duplicate email in auth.users: {}. User already exists, skipping creation.", email);
            } else if (errorMessage.contains("El email ya está registrado en perfiles de usuario")) {
                log.warn("Duplicate email in custom_users: {}. Profile already exists, skipping creation.", email);
            } else {
                log.error("Database error calling admin_create_client_transactional for email: {} | error: {}",
                        email, errorMessage, e);
            }
        }
    }

    private String extractRootCauseMessage(DataAccessException e) {
        Throwable rootCause = e;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause.getMessage() != null ? rootCause.getMessage() : e.getMessage();
    }

    @Override
    public String getEventType() {
        return "invoice.payment_succeeded";
    }
}
