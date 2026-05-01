package com.app.stripe.strategy;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerUpdateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckoutCompletedHandler implements EventHandler {

    @Override
    public void handle(Event event) {
        log.info("Handling: {}", getEventType());

        Session session = (Session) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (session == null) {
            log.warn("Could not deserialize Session from event: {}", event.getId());
            return;
        }

        // Extraer el campo WhatsApp desde custom_fields
        String whatsapp = null;
        if (session.getCustomFields() != null) {
            whatsapp = session.getCustomFields().stream()
                    .filter(f -> "whatsapp".equals(f.getKey()))
                    .findFirst()
                    .map(f -> f.getText().getValue())
                    .orElse(null);
        }

        if (whatsapp == null || whatsapp.isBlank()) {
            log.info("No WhatsApp custom field found in session: {}", session.getId());
            return;
        }

        if (session.getCustomer() == null) {
            log.warn("Session {} has no associated customer, cannot update phone", session.getId());
            return;
        }

        updateCustomerPhone(session.getCustomer(), whatsapp);
    }

    private void updateCustomerPhone(String customerId, String phone) {
        try {
            Customer customer = Customer.retrieve(customerId);
            CustomerUpdateParams params = CustomerUpdateParams.builder()
                    .setPhone(phone)
                    .build();
            customer.update(params);

            log.info("Customer {} phone updated to: {}", customerId, phone);
        } catch (StripeException e) {
            log.error("Error updating phone for customer {}: {}", customerId, e.getMessage(), e);
        }
    }

    @Override
    public String getEventType() {
        return "checkout.session.completed";
    }
}
