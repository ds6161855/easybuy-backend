package com.easybuy.service;

import org.springframework.stereotype.Service;
import java.util.List;

import com.easybuy.entity.WalletCard;
import com.easybuy.repository.WalletCardRepository;

@Service
public class WalletCardService {

    private final WalletCardRepository repo;

    public WalletCardService(WalletCardRepository repo) {
        this.repo = repo;
    }

    // ================= SAVE CARD =================
    public WalletCard saveCard(WalletCard card) {

        if (card.getNumber() == null || card.getNumber().trim().length() < 4) {
            throw new RuntimeException("Invalid card number");
        }

        if (card.getUserId() == null) {
            throw new RuntimeException("User ID required");
        }

        // 👉 clean number
        String clean = card.getNumber().replaceAll("\\s", "");

        // 👉 last 4 digits
        String last4 = clean.substring(clean.length() - 4);

        // 👉 duplicate check
        List<WalletCard> existing = repo.findByUserId(card.getUserId());
        boolean alreadyExists = existing.stream()
                .anyMatch(c -> c.getNumber().equals(last4));

        if (alreadyExists) {
            throw new RuntimeException("Card already exists");
        }

        // 👉 expiry validation
        if (card.getExpiry() == null ||
            !card.getExpiry().matches("(0[1-9]|1[0-2])/\\d{2}")) {
            throw new RuntimeException("Invalid expiry (MM/YY)");
        }

        card.setNumber(last4);
        card.setName(card.getName().trim());

        return repo.save(card);
    }

    // ================= GET CARDS =================
    public List<WalletCard> getCards(Long userId) {

        if (userId == null) {
            throw new RuntimeException("User ID required");
        }

        return repo.findByUserId(userId);
    }

    // ================= DELETE =================
    public void deleteCard(Long id) {

        if (id == null) {
            throw new RuntimeException("Card ID required");
        }

        repo.deleteById(id);
    }

    // ================= ADD MONEY =================
    public void addMoney(Long userId, Double amount) {

        if (userId == null || amount == null || amount <= 0) {
            throw new RuntimeException("Invalid data");
        }

        // 👉 user ka first card = wallet
        WalletCard wallet = repo.findByUserId(userId)
                .stream()
                .findFirst()
                .orElse(null);

        if (wallet == null) {
            throw new RuntimeException("No wallet/card found");
        }

        if (wallet.getBalance() == null) {
            wallet.setBalance(0.0);
        }

        wallet.setBalance(wallet.getBalance() + amount);

        repo.save(wallet);
    }

    // ================= GET WALLET BALANCE =================
    public Double getWalletBalance(Long userId) {

        WalletCard wallet = repo.findByUserId(userId)
                .stream()
                .findFirst()
                .orElse(null);

        if (wallet == null || wallet.getBalance() == null) {
            return 0.0;
        }

        return wallet.getBalance();
    }
}