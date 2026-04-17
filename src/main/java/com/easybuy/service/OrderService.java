package com.easybuy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easybuy.entity.EasybuyUser;
import com.easybuy.entity.Order;
import com.easybuy.entity.Product;

import com.easybuy.enums.OrderStatus;
import com.easybuy.repository.EasybuyUserRepository;
import com.easybuy.repository.OrderRepository;
import com.easybuy.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Autowired
    private EasybuyUserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }
    public Order markAsPaid(Long id) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus(OrderStatus.PAID);
        return orderRepository.save(order);
    }

    // ================= CHECK USER EXISTS =================
    public boolean userExists(Long userId){
        return userRepository.existsById(userId);
    }

    // ================= GET USER =================
    public EasybuyUser getUserById(Long userId){
        return userRepository.findById(userId).orElse(null);
    }

    // ================= PLACE ORDER =================
    @Transactional
    public Order placeOrder(Long userId,
                            Long productId,
                            int quantity,
                            double price,
                            String location) {

        EasybuyUser user = getUserById(userId);
        if(user == null) throw new RuntimeException("User not found");

        if(location == null || location.trim().isEmpty()) {
            location = user.address != null ? user.address : "Not Provided";
        }

        Order order = new Order();
        order.userId = userId;
        order.productId = productId;
        order.quantity = quantity;
        order.price = price;
        order.status = OrderStatus.PLACED;
        order.createdAt = LocalDateTime.now();
        order.location = location;

        // ✅ Save user snapshot
        order.name = user.name;
        order.mobile = user.mobile;
        order.address = user.address;

        // ✅ Estimated delivery (5 days from now)
        order.deliveryDate = LocalDateTime.now().plusDays(5);

        return orderRepository.save(order);
    }

    // ================= MARK SINGLE ORDER PAID =================
    public void markOrderPaid(Long userId){
        Order order = orderRepository.findTopByUserIdOrderByIdDesc(userId);
        if(order != null){
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);
        }
    }

    // ================= MARK ALL ACTIVE ORDERS PAID =================
    @Transactional
    public void markOrdersPaid(Long userId){
        List<Order> orders = orderRepository.findByUserIdAndStatus(userId, OrderStatus.PLACED);
        for(Order order : orders){
            order.status = OrderStatus.PAID;
            orderRepository.save(order);
        }
    }

    // ================= GET ORDERS =================
    public List<Order> getOrdersByUser(Long userId){
        return orderRepository.findByUserId(userId);
    }

    // ================= GET PRODUCT DETAILS =================
    public Product getProductDetails(Long productId){
        return productRepository.findById(productId).orElse(null);
    }

    // ================= CANCEL ORDER =================
    public boolean cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId).orElse(null);
        if(order != null && order.status == OrderStatus.PLACED){
            order.status = OrderStatus.CANCELLED;
            order.cancelDate = LocalDateTime.now();
            orderRepository.save(order);
            return true;
        }
        return false;
    }
}