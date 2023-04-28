package com.techelevator.tenmo.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Transaction {
    private int transaction_id;
    private int user_id;
    private int account_id;
    private BigDecimal amount;
    private LocalDate dateTime;
    private int target_id;
   private String status = "Approved";


    public Transaction(int transaction_id, int user_id, int account_id, BigDecimal amount, int target_id, String status) {
        this.transaction_id = transaction_id;
        this.user_id = user_id;
        this.account_id = account_id;
        this.user_id = user_id;
        this.amount = amount;
        this.target_id = target_id;
        this.status = status;
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

    public int getTarget_id() {
        return target_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setTarget_id(int target_id) {
        this.target_id = target_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
