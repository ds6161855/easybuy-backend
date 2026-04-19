package com.easybuy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.easybuy.entity.EasybuyUser;
import com.easybuy.entity.Order;
import com.easybuy.entity.Product;

import com.easybuy.enums.OrderStatus;
import com.easybuy.repository.CartRepository;
import com.easybuy.repository.OrderRepository;
import com.easybuy.service.OrderService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "https://easybuy-frontend-ochre.vercel.app")
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final CartRepository cartRepository;

    public OrderController(OrderService orderService,
                           OrderRepository orderRepository,
                           CartRepository cartRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
    }

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@RequestBody PlaceOrderRequest request){

        if(request.userId == null || request.userId <=0)
            return ResponseEntity.status(401).body("Unauthorized user");

        EasybuyUser easybuyUser = orderService.getUserById(request.userId);
        if(easybuyUser == null) return ResponseEntity.status(401).body("User not found");

        if(request.productId == null) return ResponseEntity.badRequest().body("Product id missing");
        if(request.quantity <=0) return ResponseEntity.badRequest().body("Invalid quantity");
        if(request.price <=0) return ResponseEntity.badRequest().body("Invalid price");

        Order order = orderService.placeOrder(
                request.userId,
                request.productId,
                request.quantity,
                request.price,
                request.location
        );

        return ResponseEntity.ok(order);
    }
    
    @PutMapping("/pay/{orderId}")
    public Order payOrder(@PathVariable Long orderId) {
        return orderService.markAsPaid(orderId);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrders(@PathVariable Long userId){

        if(userId == null || userId <= 0) return ResponseEntity.status(401).body("Unauthorized");
        if(!orderService.userExists(userId)) return ResponseEntity.status(401).body("User not found");

        List<OrderResponse> response = orderService.getOrdersByUser(userId)
                .stream()
                .map(order -> new OrderResponse(
                        order.id,
                        order.userId,
                        order.quantity,
                        order.price,
                        order.status.name(),
                        order.cancelDate,
                        order.createdAt,
                        orderService.getProductDetails(order.productId),
                        order.location,
                        order.name,
                        order.mobile,
                        order.address,
                        order.deliveryDate
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId){
        Order order = orderRepository.findById(orderId).orElse(null);
        if(order == null) return ResponseEntity.badRequest().body("Order not found");
        if(order.status != OrderStatus.PLACED)
            return ResponseEntity.badRequest().body("Cannot cancel order");

        order.status = OrderStatus.CANCELLED;
        order.cancelDate = LocalDateTime.now();
        orderRepository.save(order);

        // ✅ FIX: Long → String convert
        cartRepository.deleteByUserIdAndProductId(
                String.valueOf(order.userId),
                order.productId
        );

        return ResponseEntity.ok("Order cancelled");
    }

    @PostMapping("/pay/{userId}")
    public ResponseEntity<?> payOrders(@PathVariable Long userId){

        if(userId == null || userId <=0) return ResponseEntity.status(401).body("Unauthorized");
        if(!orderService.userExists(userId)) return ResponseEntity.status(401).body("User not found");

        List<Order> activeOrders = orderRepository.findByUserIdAndStatus(userId, OrderStatus.PLACED);
        if(activeOrders.isEmpty()) return ResponseEntity.badRequest().body("No active orders found");

        for(Order order : activeOrders){
            order.status = OrderStatus.PAID;
            orderRepository.save(order);

            // ✅ FIX: Long → String convert
            cartRepository.deleteByUserIdAndProductId(
                    String.valueOf(order.userId),
                    order.productId
            );
        }

        return ResponseEntity.ok("Orders marked PAID");
    }

    public static class PlaceOrderRequest {
        public Long userId;
        public Long productId;
        public int quantity;
        public double price;
        public String location;
    }

    public static class OrderResponse {
        public Long id;
        public Long userId;
        public int quantity;
        public double price;
        public String status;
        public LocalDateTime cancelDate;
        public LocalDateTime createdAt;
        public Product productDetails;
        public String location;
        public String name;
        public String mobile;
        public String address;
        public LocalDateTime deliveryDate;

        public OrderResponse(Long id, Long userId, int quantity, double price,
                             String status, LocalDateTime cancelDate, LocalDateTime createdAt,
                             Product productDetails, String location, String name, String mobile,
                             String address, LocalDateTime deliveryDate){
            this.id = id;
            this.userId = userId;
            this.quantity = quantity;
            this.price = price;
            this.status = status;
            this.cancelDate = cancelDate;
            this.createdAt = createdAt;
            this.productDetails = productDetails;
            this.location = location;
            this.name = name;
            this.mobile = mobile;
            this.address = address;
            this.deliveryDate = deliveryDate;
        }
    }
}
