package com.bankingapp.kycservice.service;

import com.bankingapp.kycservice.dto.KycVerificationRequest;
import com.bankingapp.kycservice.dto.KycVerificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class KycVerificationService {

    private static final Logger log = LoggerFactory.getLogger(KycVerificationService.class);

    // disposable email domains — reject these
    private static final Set<String> DISPOSABLE_DOMAINS = Set.of(
            "tempmail.com", "throwaway.com", "guerrillamail.com",
            "mailinator.com", "yopmail.com"
    );

    public KycVerificationResponse verify(KycVerificationRequest request)
    {

        log.info("🔍 Verifying KYC for customer: {}", request.customerId());

        // run all checks — collect reasons
        List<String> rejectionReasons = new ArrayList<>();

        // Check 1 — age verification (must be 18+)
        if (!isAgeValid(request.dateOfBirth())) {
            rejectionReasons.add("Customer must be at least 18 years old");
        }

        // Check 2 — disposable email check
        if (isDisposableEmail(request.email())) {
            rejectionReasons.add("Disposable email addresses not accepted");
        }

        // Check 3 — PAN and Aadhar should not have same digits repeated
        if (isAadharSuspicious(request.aadharNumber())) {
            rejectionReasons.add("Aadhar number appears invalid");
        }

        // Check 4 — name blacklist check
        if (isNameBlacklisted(request.name())) {
            rejectionReasons.add("Name flagged in verification system");
        }

        // Check 5 — PAN first 3 letters should match name initials
        if (!isPanNameMatch(request.panNumber(), request.name())) {
            rejectionReasons.add("PAN number does not match customer name");
        }

        // any rejection reason → reject
        if (!rejectionReasons.isEmpty()) {
            log.warn("❌ KYC rejected for: {} — Reasons: {}",
                    request.name(), rejectionReasons);
            return new KycVerificationResponse(
                    request.customerId(),
                    "REJECTED",
                    String.join(", ", rejectionReasons),
                    LocalDateTime.now()
            );
        }

        log.info("✅ KYC verified for: {}", request.name());
        return new KycVerificationResponse(
                request.customerId(),
                "VERIFIED",
                null,
                LocalDateTime.now()
        );
    }

    // Check 1 — must be 18+
    private boolean isAgeValid(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears() >= 18;
    }

    // Check 2 — disposable email
    private boolean isDisposableEmail(String email) {
        String domain = email.substring(email.indexOf('@') + 1).toLowerCase();
        return DISPOSABLE_DOMAINS.contains(domain);
    }

    // Check 3 — aadhar suspicious (all same digits like 111111111111)
    private boolean isAadharSuspicious(String aadhar) {
        return aadhar.chars().distinct().count() == 1;
    }

    // Check 4 — name blacklist
    private boolean isNameBlacklisted(String name) {
        List<String> blacklist = List.of("test", "dummy", "fake", "anonymous");
        return blacklist.stream()
                .anyMatch(word -> name.toLowerCase().contains(word));
    }

    // Check 5 — PAN first letter should match first letter of name
    // PAN format: ABCDE1234F
    // first 3 letters of PAN = surname initials in govt records
    // simplified check: first letter of PAN = first letter of name
    private boolean isPanNameMatch(String pan, String name) {
        char panFirstLetter = pan.charAt(0);
        char nameFirstLetter = Character.toUpperCase(name.charAt(0));
        return panFirstLetter == nameFirstLetter;
    }
}
