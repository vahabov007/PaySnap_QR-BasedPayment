package com.vahabvahabov.PaySnap.controller.impl;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.vahabvahabov.PaySnap.controller.StripeWebhookController;
import com.vahabvahabov.PaySnap.model.PaymentStatus;
import com.vahabvahabov.PaySnap.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/webhook")
public class StripeWebhookControllerImpl implements StripeWebhookController {

    @Autowired
    private OrderService orderService;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;


    @Override
    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String signHeader) {
        try {
            Event event = Webhook.constructEvent(payload, signHeader, webhookSecret);
            if("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                if (session != null) {
                    orderService.updateOrderStatus(session.getId(), PaymentStatus.COMPLETED);
                    log.info("Payment completed for session: {}", session.getId());
                }
            }else if("checkout.session.expired".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                if (session != null) {
                    orderService.updateOrderStatus(session.getId(), PaymentStatus.CANCELED);
                    log.info("Payment expired for session: {}", session.getId());
                }
            }
            return ResponseEntity.ok().build();
        } catch (SignatureVerificationException e) {
            log.error("Invalid webhook signature", e);
            return ResponseEntity.badRequest().build();
        }catch (Exception e) {
            log.error("Webhook error", e);
            return ResponseEntity.status(500).build();
        }
    }
}
