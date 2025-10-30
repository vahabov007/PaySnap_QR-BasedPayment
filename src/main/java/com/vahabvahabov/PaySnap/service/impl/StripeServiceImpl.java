package com.vahabvahabov.PaySnap.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.vahabvahabov.PaySnap.model.Order;
import com.vahabvahabov.PaySnap.service.StripeService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    @Value("${stripe.secret-key}")
    private String secretKey;

    @Override
    @PostConstruct
    public void init() {
        try {
            Stripe.apiKey = secretKey;
            log.info("Stripe SDK initialized with key: {}...", secretKey.substring(0, 20));
        } catch (Exception e) {
            log.error("Failed to initialize Stripe SDK: {}", e.getMessage());
            throw new RuntimeException("Stripe initialization failed", e);
        }

    }

    @Override
    public Session createdCheckoutSession(Order order, String customerEmail) throws StripeException {
        try {
            if (order.getCurrency() == null) {
                throw new IllegalArgumentException("Order currency cannot be null");
            }

            if (order.getAmount() == null || order.getAmount() <= 0) {
                throw new IllegalArgumentException("Order amount must be positive");
            }
            if (secretKey == null || !secretKey.startsWith("sk_")) {
                throw new IllegalArgumentException("Invalid Stripe secret key");
            }
            SessionCreateParams params = SessionCreateParams.builder()
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(cancelUrl + "?session_id={CHECKOUT_SESSION_ID}")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency(order.getCurrency().toLowerCase())
                                                    .setUnitAmount(order.getAmount())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Payment")
                                                                    .setDescription("Payment for order #" + order.getId())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .setClientReferenceId(order.getId().toString())
                    .setCustomerEmail(customerEmail)
                    .build();

            Session session = Session.create(params);
            log.info("Stripe session created successfully: {}", session.getId());
            return session;
        }catch (StripeException e) {
            log.error("Stripe API error - Code: {}, Message: {}, Status: {}",
                    e.getCode(), e.getMessage(), e.getStatusCode());
            log.error("Stripe error details:", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error creating Stripe session: {}", e.getMessage());
            throw new RuntimeException("Failed to create Stripe session: " + e.getMessage(), e);
        }
    }

    @Override
    public Session retrieveSession(String sessionId) throws StripeException {
        return Session.retrieve(sessionId);
    }
}
