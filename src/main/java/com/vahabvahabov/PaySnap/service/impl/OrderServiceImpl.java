package com.vahabvahabov.PaySnap.service.impl;

import com.google.zxing.WriterException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.vahabvahabov.PaySnap.dto.OrderRequest;
import com.vahabvahabov.PaySnap.dto.PaymentSessionResponse;
import com.vahabvahabov.PaySnap.model.Order;
import com.vahabvahabov.PaySnap.model.PaymentStatus;
import com.vahabvahabov.PaySnap.model.User;
import com.vahabvahabov.PaySnap.repository.OrderRepository;
import com.vahabvahabov.PaySnap.service.OrderService;
import com.vahabvahabov.PaySnap.service.QrCodeService;
import com.vahabvahabov.PaySnap.service.StripeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private StripeService stripeService;

    @Value("${session-timeout-minutes}")
    private int sessionTimeoutMinutes;

    @Override
    @Transactional
    public PaymentSessionResponse createPaymentSession(OrderRequest request, User user) throws StripeException, IOException, WriterException {
        try {
            if (request.getCurrency() == null || request.getCurrency().trim().isEmpty()) {
                throw new IllegalArgumentException("Currency cannot be null or empty");
            }
            Order order = new Order();
            order.setAmount(request.getAmount());
            order.setUser(user);
            order.setCurrency(request.getCurrency());
            order.setPaymentStatus(PaymentStatus.PENDING);
            order.setCreatedAt(LocalDateTime.now());

            Order savedOrder = orderRepository.save(order);
            log.info("Order created - ID: {}, Amount: {}, Currency: {}",
                    savedOrder.getId(), savedOrder.getAmount(), savedOrder.getCurrency());

            Session session = stripeService.createdCheckoutSession(savedOrder, request.getCustomerEmail());
            savedOrder.setStripeSessionId(session.getId());
            orderRepository.save(savedOrder);


            String qrCode = qrCodeService.generateQrCodeImage(session.getUrl(), 250, 250);
            return new PaymentSessionResponse(session.getId(), session.getUrl(), qrCode, savedOrder.getId());
        } catch (StripeException e) {
            throw new RuntimeException("StripeException is occurred.");
        } catch (WriterException e) {
            throw new RuntimeException("WriterException is occurred.");
        } catch (IOException e) {
            throw new RuntimeException("IOException is occurred.");
        }
    }

    @Override
    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    @Transactional
    public void updateOrderStatus(String stripeSessionId, PaymentStatus status) {
        orderRepository.findByStripeSessionId(stripeSessionId).ifPresent(order -> {
            order.setPaymentStatus(status);
            if (status == PaymentStatus.COMPLETED) {
                order.setCompletedAt(LocalDateTime.now());
            }
            orderRepository.save(order);
        });

    }

    @Override
    public List<Order> getExpiredPendingOrders() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(sessionTimeoutMinutes);
        return orderRepository.findByPaymentStatusAndCreatedAtBefore(PaymentStatus.PENDING, cutoffTime);
    }

    @Override
    public Optional<Order> getOrderByStripeSessionId(String stripeSessionId) {
        return orderRepository.findByStripeSessionId(stripeSessionId);
    }
}
