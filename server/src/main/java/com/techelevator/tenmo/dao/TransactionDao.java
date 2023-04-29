package com.techelevator.tenmo.dao;
import com.techelevator.tenmo.model.Transaction;

import java.security.Principal;
import java.util.List;

public interface TransactionDao {

    public List<Transaction> listTransactions(String username);

    public Transaction getTransaction(int transactionId);
    public void approveRequest(Transaction transaction, String username);
    public void denyRequest(int transactionId, String name);
    public List<Transaction> getPendingTransactions(String name);
}
