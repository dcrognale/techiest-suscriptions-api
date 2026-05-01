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

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Pong");
    }

    /**
     * Endpoint de recepción de webhooks de Stripe.
     *
     * IMPORTANTE: Stripe requiere una respuesta 2xx inmediata.
     * Cualquier código 3xx es tratado como fallo de entrega.
     * El procesamiento real se delega asincrónicamente vía EventStrategyRouter.
     *
     * @see <a href="https://docs.stripe.com/webhooks#best-practices">Stripe Webhook
     *      Best Practices</a>
     */
    @PostMapping("/process")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody byte[] payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        // 1. Verificar firma SIEMPRE antes de cualquier procesamiento
        Event event;
        try {
            event = signatureVerifier.verifyAndConstructEvent(payload, sigHeader);
        } catch (SignatureVerificationException e) {
            log.warn("Webhook signature verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }

        // 2. Idempotencia: si ya fue procesado, responder 200 de inmediato
        if (idempotencyGuard.checkAndSet(event.getId())) {
            log.info("Duplicate event ignored: [{}]", event.getId());
            return ResponseEntity.ok().build();
        }

        // 3. Procesar y marcar como completado
        try {
            eventStrategyRouter.route(event.getType(), event);
            idempotencyGuard.markCompleted(event.getId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to process webhook event [{}]: {}", event.getId(), e.getMessage(), e);
            // Retornar 500 para que Stripe reintente el evento
            return ResponseEntity.internalServerError().build();
        }
    }
}