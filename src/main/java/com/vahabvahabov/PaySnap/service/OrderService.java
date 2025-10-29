package com.vahabvahabov.PaySnap.service;

import com.google.zxing.WriterException;
import com.stripe.exception.StripeException;
import com.vahabvahabov.PaySnap.dto.OrderRequest;
import com.vahabvahabov.PaySnap.dto.PaymentSessionResponse;
import com.vahabvahabov.PaySnap.model.Order;
import com.vahabvahabov.PaySnap.model.PaymentStatus;
import com.vahabvahabov.PaySnap.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    PaymentSessionResponse createPaymentSession(OrderRequest request, User user) throws StripeException, IOException, WriterException;
    List<Order> getUserOrders(User user);
    Optional<Order> getOrderById(Long orderId);
    void updateOrderStatus(String stripeSessionId, PaymentStatus status);
    List<Order> getExpiredPendingOrders();
}
