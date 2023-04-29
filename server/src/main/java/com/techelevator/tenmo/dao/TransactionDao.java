package com.techelevator.tenmo.dao;
import com.techelevator.tenmo.model.Transaction;

import java.security.Principal;
import java.util.List;

public interface TransactionDao {

    public List<Transaction> listTransactions(String username);

    public Transaction getTransaction(int transactionId);
    List<Transaction> getPendingTransactions(int userId);
    public Transaction approveRequest(int transactionId);
    public void denyRequest(int transactionId);
}
