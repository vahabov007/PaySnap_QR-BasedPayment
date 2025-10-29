package com.vahabvahabov.PaySnap.controller.impl;


import com.vahabvahabov.PaySnap.controller.PaymentController;
import com.vahabvahabov.PaySnap.dto.OrderRequest;
import com.vahabvahabov.PaySnap.dto.PaymentSessionResponse;
import com.vahabvahabov.PaySnap.model.Order;
import com.vahabvahabov.PaySnap.model.User;
import com.vahabvahabov.PaySnap.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment")
public class PaymentControllerImpl implements PaymentController {

    @Autowired
    private OrderService orderService;

    @Override
    @PostMapping("/create")
    public ResponseEntity<?> createPaymentSession(@Valid @RequestBody OrderRequest request,
                                                  @AuthenticationPrincipal User user) {
        try {
            PaymentSessionResponse paymentSessionResponse = orderService.createPaymentSession(request, user);
            return ResponseEntity.ok(createResponse(true, "PaymentSession created successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createResponse(false, e.getMessage()));
        }

    }

    @Override
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getUserOrders(@AuthenticationPrincipal User user) {
        List<Order> orders = orderService.getUserOrders(user);
        return ResponseEntity.ok(orders);
    }

    @Override
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable(name = "orderId") Long orderId,
                                          @AuthenticationPrincipal User user) {
        Optional<Order> optional = orderService.getOrderById(orderId);
        if (optional.isPresent()){
            Order order = optional.get();
            if(!order.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(createResponse(false, "Access denied."));
            }
            return ResponseEntity.ok(order);
        }else {
            return ResponseEntity.notFound().build();
        }

    }

    private Map<String, Object> createResponse(boolean success, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        return response;
    }
}
