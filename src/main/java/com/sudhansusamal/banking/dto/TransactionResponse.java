package com.sudhansusamal.banking.dto;

import com.sudhansusamal.banking.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {

    private Long id;
    private String accountNumber;
    private String counterpartyAccountNumber;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private LocalDateTime timestamp;

    public TransactionResponse() {
    }

    public TransactionResponse(Long id, String accountNumber, String counterpartyAccountNumber,
                              TransactionType type, BigDecimal amount,
                              BigDecimal balanceAfter, LocalDateTime timestamp) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.counterpartyAccountNumber = counterpartyAccountNumber;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCounterpartyAccountNumber() {
        return counterpartyAccountNumber;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
