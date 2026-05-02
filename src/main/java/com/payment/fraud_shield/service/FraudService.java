package com.payment.fraud_shield.service;

import com.payment.fraud_shield.domain.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class FraudService {

    private final StringRedisTemplate redis;

    private static final double HIGH_AMOUNT_THRESHOLD = 1000;
    private static final int MAX_REQUESTS = 3;

    public double calculateRisk(Payment payment){

        Double riskScore = 0.0;

        if (payment.getAmount() > HIGH_AMOUNT_THRESHOLD) {
            riskScore += 0.3;
        }

        if ("NG".equals(payment.getCountry())) {
            riskScore += 0.4;
        }

        // country change
        try {
            String key1 = "fraud:last_country:" + payment.getUserId();

            String lastCountry = redis.opsForValue().get(key1);

            if (lastCountry != null && !lastCountry.equals(payment.getCountry())) {
                riskScore += 0.4;
            }

            redis.opsForValue().set(key1, payment.getCountry(), Duration.ofMinutes(10));

        } catch (Exception e) {
            System.out.println("Redis country error: " + e.getMessage());
        }

        // rate limit
        try {
            String key2 = "fraud:user:" + payment.getUserId();

            Long count = redis.opsForValue().increment(key2);

            if (count != null && count == 1) {
                redis.expire(key2, Duration.ofSeconds(60));
            }

            if (count != null && count > MAX_REQUESTS) {
                riskScore += 0.5;
            }

        } catch (Exception e) {
            System.out.println("Redis rate error: " + e.getMessage());
        }

        return Math.min(riskScore, 1.0);
    }
}
