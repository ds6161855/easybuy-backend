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
import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import javax.annotation.PostConstruct;


import io.jsonwebtoken.security.Keys; // ✅ IMPORTANT IMPORT

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EasybuyAuthService {

    private final EasybuyUserRepository repository;
    private final String SID = System.getenv("TWILIO_ACCOUNT_SID");
private final String TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
private final String SERVICE_SID = System.getenv("TWILIO_VERIFY_SERVICE_SID");

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
    @PostConstruct
public void init() {
    Twilio.init(SID, TOKEN);
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

    Verification.creator(
            SERVICE_SID,
            "+91" + trimmedMobile,
            "sms"
    ).create();

    repository.save(user);

    System.out.println("OTP SENT to " + trimmedMobile);
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

    try {
        VerificationCheck check = VerificationCheck.creator(SERVICE_SID)
                .setTo("+91" + mobile)
                .setCode(otp)
                .create();

        if ("approved".equals(check.getStatus())) {

            user.markOtpVerified();
            repository.save(user);

            return Optional.of(user);
        }

    } catch (Exception e) {
        System.out.println("OTP ERROR: " + e.getMessage());
    }

    return Optional.empty();
}


}
