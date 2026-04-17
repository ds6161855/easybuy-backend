package com.easybuy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.easybuy.entity.WalletCard;

import java.util.List;
import java.util.Optional;

public interface WalletCardRepository extends JpaRepository<WalletCard, Long> {

    // 👉 All cards of user
    List<WalletCard> findByUserId(Long userId);

    // 👉 First card (wallet use case)
    Optional<WalletCard> findFirstByUserId(Long userId);
}