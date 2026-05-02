package com.payment.fraud_shield.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Double amount;
    private String country;

    private Double riskScore;

    @Enumerated(EnumType.STRING)
    private Decision decision;

    private LocalDateTime createdAt;

    @Column(unique = true)
    private String idempotencyKey;
}
