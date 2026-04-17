package com.easybuy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easybuy.entity.Seller;
import com.easybuy.repository.SellerRepository;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class SellerService {

    @Autowired
    private SellerRepository repo;

    private String generateOtp() {
        return String.valueOf(1000 + new Random().nextInt(9000));
    }

    // 🔥 Register / Resend OTP
    public Seller register(Seller seller) {

        Seller existing = repo.findByPhone(seller.getPhone());

        String otp = generateOtp();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);

        if (existing != null) {

            // ✅ IMPORTANT FIX (update fields)
            existing.setName(seller.getName());
            existing.setEmail(seller.getEmail());
            existing.setBusinessName(seller.getBusinessName());
            existing.setGst(seller.getGst());
            existing.setCategory(seller.getCategory());

            existing.setOtp(otp);
            existing.setOtpExpiry(expiry);
            existing.setVerified(false);

            System.out.println("OTP (existing): " + otp);

            return repo.save(existing);
        }

        // New Seller
        seller.setOtp(otp);
        seller.setOtpExpiry(expiry);
        seller.setVerified(false);
        seller.setCreatedAt(LocalDateTime.now());

        System.out.println("OTP (new): " + otp);

        return repo.save(seller);
    }

    // 🔥 LOGIN OTP SEND
    public boolean sendLoginOtp(String phone) {

        if (phone == null || phone.isEmpty()) return false;

        Seller seller = repo.findByPhone(phone);

        if (seller == null || !seller.isVerified()) {
            return false;
        }

        String otp = generateOtp();
        seller.setOtp(otp);
        seller.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

        System.out.println("Login OTP: " + otp);

        repo.save(seller);
        return true;
    }

    // 🔥 LOGIN OTP VERIFY
    public boolean verifyLoginOtp(String phone, String otp) {

        if (phone == null || otp == null) return false;

        Seller seller = repo.findByPhone(phone);

        if (seller == null) return false;

        if (seller.getOtpExpiry() == null ||
            seller.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return false;
        }

        if (seller.getOtp() != null && seller.getOtp().equals(otp)) {

            seller.setOtp(null);
            seller.setOtpExpiry(null);

            repo.save(seller);
            return true;
        }

        return false;
    }

    // 🔥 Verify OTP (Register)
    public boolean verifyOtp(String phone, String otp) {

        if (phone == null || otp == null) return false;

        Seller seller = repo.findByPhone(phone);

        if (seller == null) return false;

        if (seller.getOtpExpiry() == null ||
            seller.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return false;
        }

        if (seller.getOtp() != null && seller.getOtp().equals(otp)) {

            seller.setVerified(true);
            seller.setOtp(null);
            seller.setOtpExpiry(null);

            repo.save(seller);
            return true;
        }

        return false;
    }

    // 🔥 KYC SAVE
    public void saveKyc(String phone, String pan, String gst,
                        String accountNumber, String ifsc,
                        String holderName, String address,
                        boolean bankVerified) {

        Seller seller = repo.findByPhone(phone);

        if (seller == null) {
            throw new RuntimeException("Seller not found");
        }

        seller.setPan(pan);
        seller.setGst(gst);
        seller.setAccountNumber(accountNumber);
        seller.setIfsc(ifsc);
        seller.setHolderName(holderName);
        seller.setAddress(address);
        seller.setBankVerified(bankVerified);

        repo.save(seller);
    }

    // 🔥 Get seller
    public Seller getSeller(String phone) {
        return repo.findByPhone(phone);
    }
}