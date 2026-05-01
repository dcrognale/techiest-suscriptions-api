package com.app.stripe.model;

import java.util.Map;

public record InvoiceData(
        String id,
        String currency,
        String customerEmail,
        String customerName,
        String customerPhone,
        String status,
        Long subtotal,
        Long total,
        Map<String, String> metadata
) {}
