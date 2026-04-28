package com.app.stripe.controller;

import com.app.stripe.middleware.IdempotencyGuard;
import com.app.stripe.middleware.StripeSignatureVerifier;
import com.app.stripe.router.EventStrategyRouter;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {

    private final StripeSignatureVerifier signatureVerifier;
    private final IdempotencyGuard idempotencyGuard;
    private final EventStrategyRouter eventStrategyRouter;

    @PostMapping
    public ResponseEntity<Void> handleWebhook(
            @RequestBody byte[] payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = signatureVerifier.verifyAndConstructEvent(payload, sigHeader);
        } catch (SignatureVerificationException e) {
            log.warn("Webhook signature verification failed", e);
            return ResponseEntity.badRequest().build();
        }

        if (idempotencyGuard.checkAndSet(event.getId())) {
            return ResponseEntity.ok().build();
        }

        try {
            eventStrategyRouter.route(event.getType(), event);
            idempotencyGuard.markCompleted(event.getId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to process webhook event", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
