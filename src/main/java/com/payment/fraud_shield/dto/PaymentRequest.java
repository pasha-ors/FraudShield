package com.payment.fraud_shield.dto;

public record PaymentRequest(
        Long userId,
        Double amount,
        String country,
        String idempotencyKey
){}
