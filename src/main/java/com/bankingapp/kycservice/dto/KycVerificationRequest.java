package com.bankingapp.kycservice.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

public record KycVerificationRequest(
        @NotNull
        UUID customerId,

        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
        @Pattern(regexp = "^[a-zA-Z ]+$", message = "Name must contain only letters and spaces")
        String name,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "PAN number is required")
        @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN format. Expected: ABCDE1234F")
        String panNumber,

        @NotBlank(message = "Aadhar number is required")
        @Pattern(regexp = "^[0-9]{12}$", message = "Aadhar must be exactly 12 digits")
        String aadharNumber,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^[6-9][0-9]{9}$",
                message = "Invalid Indian mobile number")
        String phoneNumber,

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth
) {}
