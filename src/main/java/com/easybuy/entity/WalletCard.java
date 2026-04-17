package com.easybuy.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wallet_cards") // ✅ good practice
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // 👉 ONLY last 4 digits stored
    @Column(nullable = false, length = 4)
    private String number;

    @Column(nullable = false)
    private String expiry;

    // ✅ WALLET BALANCE (IMPORTANT)
    @Column(nullable = false)
    private Double balance = 0.0;

    @Column(nullable = false)
    private Long userId;
}