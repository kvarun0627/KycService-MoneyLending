package com.bankingapp.kycservice.controller;

import com.bankingapp.kycservice.dto.KycVerificationRequest;
import com.bankingapp.kycservice.dto.KycVerificationResponse;
import com.bankingapp.kycservice.service.KycVerificationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/kyc")
public class KycController
{
    private final KycVerificationService kycVerificationService;

    @PostMapping("/verify")
    public ResponseEntity<KycVerificationResponse> verifyKyc(@Valid @RequestBody KycVerificationRequest kycVerificationRequest)
    {
        KycVerificationResponse response = kycVerificationService.verify(kycVerificationRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
