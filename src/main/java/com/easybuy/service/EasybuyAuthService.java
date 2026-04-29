package com.easybuy.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.easybuy.entity.AccountStatus;
import com.easybuy.entity.EasybuyUser;
import com.easybuy.repository.EasybuyUserRepository;

import io.jsonwebtoken.security.Keys;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EasybuyAuthService {

    private final EasybuyUserRepository repository;

    private static final int OTP_EXPIRY_MINUTES = 5;
    private final SecureRandom secureRandom = new SecureRandom();

    // ✅ JWT SECRET
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
            throw new RuntimeException("Mobile required");
        }

        String trimmedMobile = mobile.trim();

        EasybuyUser user = repository.findByMobile(trimmedMobile)
                .orElseGet(() -> EasybuyUser.builder()
                        .mobile(trimmedMobile)
                        .accountStatus(AccountStatus.ACTIVE)
                        .otpVerified(false)
                        .build());

        // 🔢 OTP generate
        String otp = String.valueOf(1000 + secureRandom.nextInt(9000));

        try {
            String url = "https://www.fast2sms.com/dev/bulkV2";

UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
        .queryParam("authorization", System.getenv("FAST2SMS_API_KEY"))
        .queryParam("route", "otp")
        .queryParam("variables_values", otp)
        .queryParam("flash", "0")
        .queryParam("numbers", trimmedMobile);

RestTemplate restTemplate = new RestTemplate();

ResponseEntity<String> response = restTemplate.getForEntity(
        builder.toUriString(),
        String.class
);

            System.out.println("OTP SENT: " + otp);
            System.out.println("Fast2SMS RESPONSE: " + response.getBody());

            // 💾 OTP store
            user.generateOtp(otp);

            repository.save(user);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("OTP sending failed");
        }
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

        if (mobile == null || otp == null) {
            return Optional.empty();
        }

        mobile = mobile.trim();
        otp = otp.trim();

        Optional<EasybuyUser> optionalUser = repository.findByMobile(mobile);

        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        EasybuyUser user = optionalUser.get();

        // ❌ OTP not present
       if (user.getOtp() == null || user.getOtpGeneratedAt() == null) {
    return Optional.empty();
}

// ⏱ expiry check
if (user.getOtpGeneratedAt().plusMinutes(OTP_EXPIRY_MINUTES)
        .isBefore(LocalDateTime.now())) {
    return Optional.empty();
}

        // 🔐 OTP match
        if (otp.equals(user.getOtp())) {

              user.markOtpVerified();

            repository.save(user);

            return Optional.of(user);
        }

        return Optional.empty();
    }
}
