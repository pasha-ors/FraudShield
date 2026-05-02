package com.payment.fraud_shield.service;

import com.payment.fraud_shield.domain.Decision;
import org.springframework.stereotype.Service;

@Service
public class DecisionService {

    public Decision makeDecision(Double riskScore){
        if(riskScore < 0.5){
            return Decision.ALLOW;
        }

        if(riskScore < 0.8){
            return Decision.REVIEW;
        }

        return Decision.BLOCK;
    }

}
