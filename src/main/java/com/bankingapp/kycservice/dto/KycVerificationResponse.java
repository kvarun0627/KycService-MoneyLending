package com.bankingapp.kycservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

// KycVerificationResponse.java
public record KycVerificationResponse(
        UUID customerId,
        String status,      // VERIFIED or REJECTED
        String reason,      // why rejected (if applicable)
        LocalDateTime verifiedAt
) {}
