package com.app.stripe.middleware;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.net.Webhook;
import com.stripe.model.Event;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;

@Component
@ConfigurationProperties(prefix = "stripe")
public class StripeSignatureVerifier {

    private String webhookSecret;

    public void setWebhookSecret(String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    public Event verifyAndConstructEvent(byte[] payload, String sigHeader) throws SignatureVerificationException {
        String payloadString = new String(payload, StandardCharsets.UTF_8);
        return Webhook.constructEvent(payloadString, sigHeader, webhookSecret);
    }
}
