package com.vahabvahabov.PaySnap.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentSessionResponse {

    private String sessionId;
    private String paymentUrl;
    private String qrCodeImage;
    private Long orderId;

}
