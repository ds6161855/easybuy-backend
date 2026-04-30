package com.easybuy.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

    // 🔥 DEMO OTP (fixed)
   String otp = String.valueOf(100000 + secureRandom.nextInt(900000));
System.out.println("DEMO OTP for " + mobile + " : " + otp);

    // 💾 DB में save
    user.generateOtp(otp);
    repository.save(user);
}

    public List<EasybuyUser> getAllUsers() {
    return repository.findAll();
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
             user.setOtp(null);
    user.setOtpGeneratedAt(null);

            repository.save(user);

            return Optional.of(user);
        }

        return Optional.empty();
    }
}
