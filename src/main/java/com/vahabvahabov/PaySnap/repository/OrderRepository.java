package com.vahabvahabov.PaySnap.repository;

import com.vahabvahabov.PaySnap.model.Order;
import com.vahabvahabov.PaySnap.model.PaymentStatus;
import com.vahabvahabov.PaySnap.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    List<Order> findByPaymentStatusAndCreatedAtBefore(PaymentStatus status, LocalDateTime localDateTime);
    Optional<Order> findByStripeSessionId(String stripeSessionId);
    @Query("SELECT o FROM Order o WHERE o.user = :user AND o.paymentStatus = :status")
    List<Order> findByUserAndPaymentStatus(@Param("user") User user, @Param("status") PaymentStatus status);
}
