package com.vahabvahabov.PaySnap.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {

    @NotNull(message = "Amount is required.")
    @Min(value = 1, message = "Amount must be greater than 0.")
    private Long amount;

    @NotEmpty(message = "Currency is required.")
    private String currency;

    private String description;

    private String customerEmail;


}
