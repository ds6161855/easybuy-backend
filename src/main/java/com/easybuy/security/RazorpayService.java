package com.easybuy.security;


import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import java.util.HashMap;
import java.util.Map;

@Service
public class RazorpayService {

    private final RazorpayClient client;
    private final RazorpayConfig razorpayConfig;

    public RazorpayService(RazorpayClient client, RazorpayConfig razorpayConfig) {
        this.client = client;
        this.razorpayConfig = razorpayConfig;
    }
    
    public String getRazorpaySecret() {
        return razorpayConfig.secret;
    }

    public Map<String, Object> createOrder(int amount) {
        Map<String, Object> response = new HashMap<>();
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount);
            orderRequest.put("currency", "INR");
            orderRequest.put("payment_capture", 1);

            Order order = client.orders.create(orderRequest);

            response.put("id", order.get("id"));
            response.put("amount", order.get("amount"));
            response.put("currency", order.get("currency"));
            response.put("razorpayKey", razorpayConfig.getKey()); // directly access
        } catch (RazorpayException e) {
            e.printStackTrace();
            response.put("error", e.getMessage());
        }
        return response;
    }


    public String createOrder1(int amount) {
        return "dummy_order_" + System.currentTimeMillis();
    }
}

