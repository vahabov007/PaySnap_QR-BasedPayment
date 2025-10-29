package com.vahabvahabov.PaySnap.model;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING("Pending"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    CANCELED("Canceled");

    private final String displayInfo;

    PaymentStatus(String displayInfo) {
        this.displayInfo = displayInfo;
    }


}
