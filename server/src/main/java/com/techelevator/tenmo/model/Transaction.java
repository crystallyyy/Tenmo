package com.techelevator.tenmo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private int transaction_id;
    private int account_id;
    private BigDecimal amount;
    private LocalDateTime dateTime;
    private int target_id;
   // private String status;

    public Transaction(int transaction_id, int account_id, BigDecimal amount, LocalDateTime dateTime, int target_id) {
        this.transaction_id = transaction_id;
        this.account_id = account_id;
        this.amount = amount;
        this.dateTime = dateTime;
        this.target_id = target_id;
    }

    public Transaction(){}

    public int getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getTarget_id() {
        return target_id;
    }

    public void setTarget_id(int target_id) {
        this.target_id = target_id;
    }
}
