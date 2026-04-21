package com.easybuy.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.easybuy.entity.EasybuyUser;
import com.easybuy.service.EasybuyAuthService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "https://easybuy-frontend-ochre.vercel.app")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class EasybuyAuthController {

    private final EasybuyAuthService service;

    // ================= SEND OTP =================
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> body,
                                     @RequestParam(defaultValue = "false") boolean isLogin) {

        try {
            String mobile = body.get("mobile");

if (mobile == null || mobile.trim().isEmpty()) {
    return ResponseEntity.badRequest().body(
            Map.of("success", false, "message", "Mobile is required")
    );
}

mobile = mobile.trim();

if (!mobile.matches("^[6-9]\\d{9}$")) {
    return ResponseEntity.badRequest().body(
            Map.of("success", false, "message", "Invalid mobile number")
    );
}

            if (isLogin && !service.isMobileRegistered(mobile)) {
                return ResponseEntity.badRequest().body(
                        Map.of("success", false, "message", "User not registered")
                );
            }

            if (!isLogin && service.isMobileRegistered(mobile)) {
                return ResponseEntity.badRequest().body(
                        Map.of("success", false, "message", "Mobile already registered")
                );
            }

            service.sendOtp(mobile);

            return ResponseEntity.ok(
                    Map.of("success", true, "message", "OTP Sent Successfully")
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    Map.of("success", false, "message", "Internal Server Error: " + e.getMessage())
            );
        }
    }

    // ================= GET USER =================
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {

        try {
            EasybuyUser user = service.getUser(id);

            if (user == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("success", false, "message", "User not found")
                );
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("name", user.getName());
            response.put("mobile", user.getMobile());
            response.put("email", user.getEmail());
            response.put("address", user.getAddress());
            response.put("pan", user.getPan());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    Map.of("success", false, "message", "Error fetching user")
            );
        }
    }

    // ================= UPDATE PROFILE =================
    @PostMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> body) {

        try {
            if (body.get("userId") == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("success", false, "message", "UserId required")
                );
            }

            Long userId = Long.valueOf(body.get("userId").toString());

            String name = body.get("name") != null ? body.get("name").toString() : null;
            String email = body.get("email") != null ? body.get("email").toString() : null;
            String address = body.get("address") != null ? body.get("address").toString() : null;
            String pan = body.get("pan") != null ? body.get("pan").toString() : null;

            EasybuyUser user = service.updateProfile(userId, name, email, address, pan);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Profile Updated");
            response.put("userId", user.getId());
            response.put("name", user.getName());
            response.put("mobile", user.getMobile());
            response.put("email", user.getEmail());
            response.put("address", user.getAddress());
            response.put("pan", user.getPan());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Profile Update Failed: " + e.getMessage())
            );
        }
    }

    // ================= VERIFY OTP =================
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> body) {

        try {
            String mobile = body.get("mobile");
String otp = body.get("otp");

if (mobile == null || otp == null || 
    mobile.trim().isEmpty() || otp.trim().isEmpty()) {

    return ResponseEntity.badRequest().body(
            Map.of("success", false, "message", "Mobile and OTP required")
    );
}

mobile = mobile.trim();
otp = otp.trim();

            return service.verifyOtp(mobile, otp)
                    .map(user -> {

                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("userId", user.getId());
                        response.put("name", user.getName());
                        response.put("mobile", user.getMobile());
                        response.put("email", user.getEmail());
                        response.put("address", user.getAddress());
                        response.put("pan", user.getPan());
                        response.put("message", "Login Successful");

                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> ResponseEntity.badRequest().body(
                            Map.of("success", false, "message", "Invalid or Expired OTP")
                    ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    Map.of("success", false, "message", "OTP Verification Failed: " + e.getMessage())
            );
        }
    
    }
}
