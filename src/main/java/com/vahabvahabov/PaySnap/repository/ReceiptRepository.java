package com.vahabvahabov.PaySnap.repository;

import com.vahabvahabov.PaySnap.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
}
