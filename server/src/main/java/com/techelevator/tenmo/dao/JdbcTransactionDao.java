package com.techelevator.tenmo.dao;

import java.util.ArrayList;

public class JdbcTransactionDao implements TransactionDao {

    public List<Transaction> listTransactions() {
        List<Transaction> transactionList = new ArrayList<>();
        String sql = "SELECT "
    }

    public Transaction getTransaction(int transactionId) {

    }
}
