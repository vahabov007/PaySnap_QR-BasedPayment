package com.vahabvahabov.PaySnap.controller;

import org.springframework.http.ResponseEntity;

public interface StripeWebhookController {

    public ResponseEntity<String> handleStripeWebhook(String payload, String signHeader);
}
