package com.app.stripe.model;

public record InvoiceData(
        String id,
        String currency,
        String customerEmail,
        String customerName,
        String status,
        Long subtotal,
        Long total
) {}
