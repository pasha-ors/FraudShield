package com.payment.fraud_shield.kafka;

import com.payment.fraud_shield.domain.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FraudEventProducer {

    private final KafkaTemplate<String, Payment> kafka;

    public void sendToReview(Payment payment){
        kafka.send("fraud-review", payment);
    }


    public void sendBlocked(Payment payment){
        kafka.send("fraud-blocked", payment);
    }

}
