package com.app.stripe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StripeWebhookApplication {
    public static void main(String[] args) {
        SpringApplication.run(StripeWebhookApplication.class, args);
    }
}
