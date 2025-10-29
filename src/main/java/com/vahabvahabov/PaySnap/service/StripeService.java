package com.vahabvahabov.PaySnap.service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.vahabvahabov.PaySnap.model.Order;

public interface StripeService {

    public void init();

    public Session createdCheckoutSession(Order order, String customerEmail) throws StripeException;

    public Session retrieveSession(String sessionId) throws StripeException;


}
