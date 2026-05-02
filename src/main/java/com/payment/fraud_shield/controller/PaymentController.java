package com.payment.fraud_shield.controller;

import com.payment.fraud_shield.domain.Payment;
import com.payment.fraud_shield.dto.PaymentRequest;
import com.payment.fraud_shield.dto.PaymentResponse;
import com.payment.fraud_shield.repository.PaymentRepository;
import com.payment.fraud_shield.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    @PostMapping
    public PaymentResponse create(@RequestBody PaymentRequest request){
        return paymentService.process(request);
    }

    @GetMapping
    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }
}
