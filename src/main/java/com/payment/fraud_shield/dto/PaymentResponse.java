package com.payment.fraud_shield.dto;

public record PaymentResponse(
        Long paymentId,
        Double riskScore,
        String decision
) {}
