package com.easybuy.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @Column(unique = true, nullable = false)
    private String phone;

    private String businessName;
    private String gst;
    private String category;
    

    private String otp;
    private boolean verified;
    private String pan;
    private String accountNumber;
    private String ifsc;
    private String holderName;
    private String address;

    private boolean bankVerified;

    private LocalDateTime otpExpiry;   // 🔥 NEW
    private LocalDateTime createdAt;   // 🔥 NEW
}