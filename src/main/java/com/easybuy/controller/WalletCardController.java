package com.easybuy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

import com.easybuy.entity.WalletCard;
import com.easybuy.service.WalletCardService;

@RestController
@RequestMapping("/api/cards")
@CrossOrigin(origins = "*")
public class WalletCardController {

    private final WalletCardService service;

    public WalletCardController(WalletCardService service) {
        this.service = service;
    }

    // ================= SAVE =================
    @PostMapping
    public WalletCard saveCard(@RequestBody WalletCard card) {
        return service.saveCard(card);
    }

    // ================= GET =================
    @GetMapping("/{userId}")
    public List<WalletCard> getCards(@PathVariable Long userId) {
        return service.getCards(userId);
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public String deleteCard(@PathVariable Long id) {
        service.deleteCard(id);
        return "Deleted";
    }

    // ================= ADD MONEY =================
    @PostMapping("/add-money")
    public ResponseEntity<?> addMoney(@RequestBody Map<String, Object> data) {

        Long userId = Long.valueOf(data.get("userId").toString());
        Double amount = Double.valueOf(data.get("amount").toString());

        service.addMoney(userId, amount);

        return ResponseEntity.ok("Money added");
    }

    // ================= GET WALLET =================
    @GetMapping("/wallet/{userId}")
    public Double getWalletBalance(@PathVariable Long userId) {
        return service.getWalletBalance(userId);
    }
}