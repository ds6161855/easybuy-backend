package com.easybuy.dto;

import com.easybuy.entity.Seller;

public class SellerDTO {

    private String name;
    private String email;
    private String phone;
    private String businessName;
    private String gst;
    private String category;

    private String pan;
    private String accountNumber;
    private String ifsc;
    private String holderName;
    private String address;

    private boolean verified;
    private boolean bankVerified;

    // 🔥 Constructor (Entity → DTO)
    public SellerDTO(Seller seller) {
        this.name = seller.getName();
        this.email = seller.getEmail();
        this.phone = seller.getPhone();
        this.businessName = seller.getBusinessName();
        this.gst = seller.getGst();
        this.category = seller.getCategory();

        this.pan = seller.getPan();
        this.accountNumber = seller.getAccountNumber();
        this.ifsc = seller.getIfsc();
        this.holderName = seller.getHolderName();
        this.address = seller.getAddress();

        this.verified = seller.isVerified();
        this.bankVerified = seller.isBankVerified();
    }

    // 🔥 Getters
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getBusinessName() { return businessName; }
    public String getGst() { return gst; }
    public String getCategory() { return category; }

    public String getPan() { return pan; }
    public String getAccountNumber() { return accountNumber; }
    public String getIfsc() { return ifsc; }
    public String getHolderName() { return holderName; }
    public String getAddress() { return address; }

    public boolean isVerified() { return verified; }
    public boolean isBankVerified() { return bankVerified; }
}