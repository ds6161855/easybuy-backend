package com.easybuy.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easybuy.entity.AccountStatus;
import com.easybuy.entity.EasybuyUser;
import com.easybuy.repository.EasybuyUserRepository;


import io.jsonwebtoken.security.Keys; // ✅ IMPORTANT IMPORT

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EasybuyAuthService {

    private final EasybuyUserRepository repository;

    private static final int OTP_EXPIRY_MINUTES = 5;
    private final SecureRandom secureRandom = new SecureRandom();

    // ✅ PROPER SECRET KEY (at least 32 chars)
    private static final String SECRET = "mySuperSecretKeyForJwtToken12345678901234567890";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // ================= CHECK MOBILE =================
    public boolean isMobileRegistered(String mobile) {
        if (mobile == null || mobile.trim().isEmpty()) return false;
        return repository.findByMobile(mobile.trim()).isPresent();
    }

    // ================= GET USER =================
    public EasybuyUser getUser(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ================= SEND OTP =================
    @Transactional
    public void sendOtp(String mobile) {

        if (mobile == null || mobile.trim().isEmpty()) {
            throw new RuntimeException("Mobile number required");
        }

        String trimmedMobile = mobile.trim();

        EasybuyUser user = repository.findByMobile(trimmedMobile)
                .orElseGet(() -> EasybuyUser.builder()
                        .mobile(trimmedMobile)
                        .accountStatus(AccountStatus.ACTIVE)
                        .otpVerified(false)
                        .build());

        String otp = String.valueOf(100000 + secureRandom.nextInt(900000));

        user.generateOtp(otp);

        repository.save(user);

        // ✅ IMPORTANT DEBUG
        System.out.println("🔥 GENERATED OTP for " + trimmedMobile + " = " + otp);
    }

    // ================= UPDATE PROFILE =================
    @Transactional
    public EasybuyUser updateProfile(Long userId, String name, String email, String address, String pan) {

        EasybuyUser user = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (name != null) user.setName(name);
        if (email != null) user.setEmail(email);
        if (address != null) user.setAddress(address);
        if (pan != null) user.setPan(pan);

        return repository.save(user);
    }

    // ================= VERIFY OTP =================
    @Transactional
    public Optional<EasybuyUser> verifyOtp(String mobile, String otp) {

        // ✅ BASIC VALIDATION
        if (mobile == null || otp == null || mobile.trim().isEmpty() || otp.trim().isEmpty()) {
            System.out.println("❌ Mobile or OTP missing");
            return Optional.empty();
        }

        mobile = mobile.trim();
        otp = otp.trim();

        Optional<EasybuyUser> optionalUser = repository.findByMobile(mobile);

        if (optionalUser.isEmpty()) {
            System.out.println("❌ User not found for mobile: " + mobile);
            return Optional.empty();
        }

        EasybuyUser user = optionalUser.get();

        // ✅ DEBUG LOGS (IMPORTANT)
        System.out.println("👉 ENTERED OTP: " + otp);
        System.out.println("👉 STORED OTP: " + user.getOtp());
        System.out.println("👉 OTP GENERATED AT: " + user.getOtpGeneratedAt());

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            System.out.println("❌ User not active");
            return Optional.empty();
        }

        if (user.getOtp() == null || user.getOtpGeneratedAt() == null) {
            System.out.println("❌ OTP not generated");
            return Optional.empty();
        }

        boolean notExpired =
                LocalDateTime.now().isBefore(
                        user.getOtpGeneratedAt().plusMinutes(OTP_EXPIRY_MINUTES)
                );

        boolean validOtp = otp.equals(user.getOtp());

        System.out.println("👉 OTP MATCH: " + validOtp);
        System.out.println("👉 OTP NOT EXPIRED: " + notExpired);

        if (validOtp && notExpired) {

            user.markOtpVerified();

            // ✅ CLEAR OTP AFTER SUCCESS
            user.setOtp(null);
            user.setOtpGeneratedAt(null);

            repository.save(user);

            System.out.println("✅ OTP VERIFIED SUCCESS");

            return Optional.of(user);
        }

        System.out.println("❌ OTP INVALID OR EXPIRED");

        return Optional.empty();
    }


}