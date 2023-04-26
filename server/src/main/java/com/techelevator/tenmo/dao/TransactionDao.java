package com.techelevator.tenmo.dao;
import com.techelevator.tenmo.model.Transaction;

public interface TransactionDao {

    public List<Transaction> listTransactions();

    public Transaction getTransaction(int transactionId);
}
