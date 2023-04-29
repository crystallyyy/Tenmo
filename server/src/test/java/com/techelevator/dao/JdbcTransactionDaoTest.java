package com.techelevator.dao;

import com.techelevator.tenmo.dao.Account;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransactionDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;


import java.util.List;

import static org.junit.Assert.*;

public class JdbcTransactionDaoTest extends BaseDaoTests {
    private JdbcTransactionDao sut;
    private JdbcUserDao user;
    private JdbcAccountDao account;



    @Before
    public void setUp() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        sut = new JdbcTransactionDao(jdbcTemplate);
        user = new JdbcUserDao(jdbcTemplate);
        account = new JdbcAccountDao(jdbcTemplate);
    }

    @Test
    public void listTransactions() {
        List<Transaction> transactionExpectedList = new ArrayList<>();
        Transaction transaction = new Transaction(3001, 1001, 2001, new BigDecimal("200.00"), 1002, "Approved");
        transactionExpectedList.add(transaction);
        user.create("Test", "password");
        user.create("Tester2", "4000");
        Transaction actualTransaction = sut.createTransaction(new Transaction(3001,1001,2001, new BigDecimal("200.00"), 1002, "Approved"));
        List<Transaction> actualList = sut.listTransactions(account.getAccount(actualTransaction.getUser_id()));
        //List<Transaction> transactions = sut.listTransactions()
        //assertEquals(size)
        //assertTransactionsMatch
    }

    @Test
    public void getTransaction() {
    }
}