package com.sudhansusamal.banking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AmountRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", inclusive = false, message = "Amount must be greater than zero")
    private BigDecimal amount;

    public AmountRequest() {
    }

    public AmountRequest(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
