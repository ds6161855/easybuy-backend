package com.easybuy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.easybuy.entity.Order;
import com.easybuy.enums.OrderStatus;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    List<Order> findByUserIdAndStatusNot(Long userId, OrderStatus status);

    Order findTopByUserIdOrderByIdDesc(Long userId);
}