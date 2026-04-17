package com.easybuy.entity;

import java.time.LocalDateTime;
import com.easybuy.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderId;

    @Column(unique = true)
    private String paymentId;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    // ✅ User snapshot
    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private String mobile;

    @Column(nullable = true)
    private String address;

    @PrePersist
    public void prePersist() {
        this.paymentDate = LocalDateTime.now();
    }
}