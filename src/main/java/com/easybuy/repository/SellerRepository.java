package com.easybuy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.easybuy.entity.Seller;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    Seller findByPhone(String phone);
}