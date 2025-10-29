package com.vahabvahabov.PaySnap.controller;

import com.vahabvahabov.PaySnap.dto.OrderRequest;
import com.vahabvahabov.PaySnap.model.Order;
import com.vahabvahabov.PaySnap.model.User;
import org.aspectj.weaver.ast.Or;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PaymentController {

    public ResponseEntity<?> createPaymentSession(OrderRequest request, User user);

    public ResponseEntity<List<Order>> getUserOrders(User user);

    public ResponseEntity<?> getOrderById(Long orderId, User user);
}
