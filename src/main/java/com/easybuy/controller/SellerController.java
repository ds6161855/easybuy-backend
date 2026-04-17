package com.easybuy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.easybuy.entity.Seller;
import com.easybuy.service.SellerService;
import com.easybuy.dto.SellerDTO; // ✅ DTO ADD

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/seller")
@CrossOrigin(origins = "*")
public class SellerController {

    @Autowired
    private SellerService service;

    // 🔥 REGISTER / SEND OTP
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Seller seller) {

        Map<String, Object> res = new HashMap<>();

        try {
            if (seller.getPhone() == null || seller.getPhone().isEmpty()) {
                throw new RuntimeException("Phone is required");
            }

            Seller saved = service.register(seller);

            res.put("success", true);
            res.put("message", "OTP Sent Successfully");
            res.put("phone", saved.getPhone());

        } catch (Exception e) {
            res.put("success", false);
            res.put("message", e.getMessage());
        }

        return res;
    }

    // 🔥 LOGIN - SEND OTP
    @PostMapping("/login/send-otp")
    public Map<String, Object> sendLoginOtp(@RequestBody Map<String, String> req) {

        Map<String, Object> res = new HashMap<>();

        try {
            String phone = req.get("phone");

            if (phone == null || phone.isEmpty()) {
                throw new RuntimeException("Phone is required");
            }

            boolean sent = service.sendLoginOtp(phone);

            if (sent) {
                res.put("success", true);
                res.put("message", "OTP Sent Successfully");
            } else {
                res.put("success", false);
                res.put("message", "Seller not found or not verified");
            }

        } catch (Exception e) {
            res.put("success", false);
            res.put("message", e.getMessage());
        }

        return res;
    }

    // 🔥 LOGIN - VERIFY OTP
    @PostMapping("/login/verify")
    public Map<String, Object> verifyLoginOtp(@RequestBody Map<String, String> req) {

        Map<String, Object> res = new HashMap<>();

        try {
            String phone = req.get("phone");
            String otp = req.get("otp");

            boolean success = service.verifyLoginOtp(phone, otp);

            if (success) {
                Seller seller = service.getSeller(phone);

                // ✅ DTO USE (SECURITY FIX)
                SellerDTO dto = new SellerDTO(seller);

                res.put("success", true);
                res.put("message", "Login Success");
                res.put("seller", dto);
            } else {
                res.put("success", false);
                res.put("message", "Invalid or expired OTP");
            }

        } catch (Exception e) {
            res.put("success", false);
            res.put("message", e.getMessage());
        }

        return res;
    }

    // 🔥 REGISTER VERIFY OTP
    @PostMapping("/verify")
    public Map<String, Object> verify(@RequestBody Map<String, String> req) {

        Map<String, Object> res = new HashMap<>();

        try {
            String phone = req.get("phone");
            String otp = req.get("otp");

            if (phone == null || otp == null) {
                throw new RuntimeException("Phone and OTP required");
            }

            boolean success = service.verifyOtp(phone, otp);

            if (success) {
                Seller seller = service.getSeller(phone);

                // ✅ DTO USE
                SellerDTO dto = new SellerDTO(seller);

                res.put("success", true);
                res.put("message", "Seller Verified Successfully");
                res.put("seller", dto);
            } else {
                res.put("success", false);
                res.put("message", "Invalid or Expired OTP");
            }

        } catch (Exception e) {
            res.put("success", false);
            res.put("message", e.getMessage());
        }

        return res;
    }

    // 🔥 KYC SAVE
    @PostMapping("/kyc")
    public Map<String, Object> saveKyc(@RequestBody Map<String, String> req) {

        Map<String, Object> res = new HashMap<>();

        try {
            String phone = req.get("phone");

            if (phone == null || phone.isEmpty()) {
                throw new RuntimeException("Phone is required");
            }

            String pan = req.get("pan");
            String gst = req.get("gst");
            String accountNumber = req.get("accountNumber");
            String ifsc = req.get("ifsc");
            String holderName = req.get("holderName");
            String address = req.get("address");

            boolean bankVerified = false;

            if (accountNumber != null && accountNumber.length() >= 9 &&
                ifsc != null && ifsc.length() == 11) {

                bankVerified = true;
            }

            service.saveKyc(phone, pan, gst, accountNumber, ifsc, holderName, address, bankVerified);

            res.put("success", true);
            res.put("bankVerified", bankVerified);
            res.put("message", "KYC Saved Successfully");

        } catch (Exception e) {
            res.put("success", false);
            res.put("message", e.getMessage());
        }

        return res;
    }
}