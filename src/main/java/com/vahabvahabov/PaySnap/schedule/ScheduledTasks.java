package com.vahabvahabov.PaySnap.service;

import com.vahabvahabov.PaySnap.model.Order;
import com.vahabvahabov.PaySnap.model.PaymentStatus;
import com.vahabvahabov.PaySnap.repository.BlackListRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ScheduledTasks {

    @Autowired
    private OrderService orderService;
    @Autowired
    private BlackListRepository tokenBlacklistRepository;


    @Scheduled(fixedRate = 300000)  // 5 minutes
    public void cleanupExpiredPaymentSessions() {
        List<Order> expiredOrders = orderService.getExpiredPendingOrders();
        for (Order order : expiredOrders) {
            order.setPaymentStatus(PaymentStatus.CANCELED);
            log.info("Marked order {} as canceled due to expiration", order.getId());
        }
    }

    @Scheduled(fixedRate = 3600000) // Every 1 hour
    public void cleanupExpiredBlacklistedTokens() {
        log.debug("Token blacklist cleanup completed");
    }
}