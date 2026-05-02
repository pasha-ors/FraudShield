package com.payment.fraud_shield.service;

import com.payment.fraud_shield.domain.Decision;
import com.payment.fraud_shield.domain.Payment;
import com.payment.fraud_shield.dto.PaymentRequest;
import com.payment.fraud_shield.dto.PaymentResponse;
import com.payment.fraud_shield.kafka.FraudEventProducer;
import com.payment.fraud_shield.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final FraudService fraudService;
    private final DecisionService decisionService;
    private final PaymentRepository paymentRepository;
    private final FraudEventProducer producer;

    public PaymentResponse process(PaymentRequest request) {

        try {
            Payment payment = mapToDomain(request);

            double riskScore = fraudService.calculateRisk(payment);
            Decision decision = decisionService.makeDecision(riskScore);

            payment.setRiskScore(riskScore);
            payment.setDecision(decision);
            payment.setCreatedAt(LocalDateTime.now());

            paymentRepository.save(payment);

            PaymentResponse response = new PaymentResponse(
                    payment.getId(),
                    riskScore,
                    decision.name()
            );

            if(decision == Decision.ALLOW){
                return response;
            }

            if(decision == Decision.REVIEW){
                producer.sendToReview(payment);
            }

            if(decision == Decision.BLOCK){
                producer.sendBlocked(payment);
            }

            return response;

        } catch (DataIntegrityViolationException e){

            Payment p = paymentRepository
                    .findByIdempotencyKey(request.idempotencyKey())
                    .orElseThrow();

            return new PaymentResponse(
                    p.getId(),
                    p.getRiskScore(),
                    p.getDecision().name()
            );
        }

    }

    private Payment mapToDomain(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setUserId(request.userId());
        payment.setAmount(request.amount());
        payment.setCountry(request.country());
        payment.setIdempotencyKey(request.idempotencyKey());
        return payment;
    }

}
