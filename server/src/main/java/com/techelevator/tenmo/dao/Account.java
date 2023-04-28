package com.techelevator.tenmo.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class Account {
    private int account_id;

    private int user_id;

    private BigDecimal balance = new BigDecimal(1000);
    private boolean is_primary;

    public Account(int account_id, int user_id, BigDecimal balance, boolean is_primary) {
        this.account_id = account_id;
        this.user_id = user_id;
        this.balance = balance;
        this.is_primary = is_primary;
    }

    public Account(){}

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public boolean isIs_primary() {
        return is_primary;
    }

    public void setIs_primary(boolean is_primary) {
        this.is_primary = is_primary;
    }
}
