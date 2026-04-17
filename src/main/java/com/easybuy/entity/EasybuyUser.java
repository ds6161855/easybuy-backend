package com.easybuy.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "easybuy_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EasybuyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 10)
	public String mobile;

    public String name;

    private String email;

    @Column(length = 500)
	public String address;

    private String pan;

    private String otp;

    private LocalDateTime otpGeneratedAt;

    @Column(nullable = false)
    private boolean otpVerified;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus accountStatus;

    @PrePersist
    public void prePersist() {
        if (accountStatus == null) {
            accountStatus = AccountStatus.ACTIVE;
        }
        otpVerified = false;
    }

    public void generateOtp(String otp) {
        this.otp = otp;
        this.otpGeneratedAt = LocalDateTime.now();
        this.otpVerified = false;
    }

    public void markOtpVerified() {
        this.otpVerified = true;
        this.otp = null;
        this.otpGeneratedAt = null;
    }
}