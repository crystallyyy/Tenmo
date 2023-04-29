package com.techelevator.dao;

import com.techelevator.tenmo.dao.Account;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransactionDao;
import com.techelevator.tenmo.dao.JdbcUserDao;

import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import java.math.BigDecimal;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class JdbcTransactionDaoTest extends BaseDaoTests {
    private JdbcTransactionDao sut;
    private JdbcUserDao user;
    private JdbcAccountDao account;
    private Transaction test;
    private Account testAccount;

    @Before
    public void setUp() {


        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcTransactionDao(jdbcTemplate);


        sut = new JdbcTransactionDao(jdbcTemplate);
        user = new JdbcUserDao(jdbcTemplate);
        account = new JdbcAccountDao(jdbcTemplate);
        testAccount = new Account(2003,1002, new BigDecimal("500.00"),true);
        test = new Transaction(3001,1001,2001, new BigDecimal("200.00"), 1002, "Approved");
    }
    @Test
    public void getTransactions_regular_test() {
        user.create("Tester1", "password");
        Transaction expected = sut.createTransaction(new Transaction(3001,1001,2001, new BigDecimal("200.00"), 1002, "Approved"));
        assertTransactionsMatch(sut.getTransaction(3001), expected);
    }

    @Test
    public void listTransactions_one_in_list() {
        List<Transaction> transactionExpectedList = new ArrayList<>();
        transactionExpectedList.add(test);
        user.create("Tester1", "password");
        Transaction actualTransaction = sut.createTransaction(new Transaction(3001,1001,2001, new BigDecimal("200"), 1002, "Approved"));
        List<Transaction> actualList = sut.listTransactions("Tester1");
        assertTransactionsMatch(transactionExpectedList.get(0), actualList.get(0));

    }
    @Test
    public void listTransactions_large_list() {
        List<Transaction> transactionExpectedList = new ArrayList<>();
        transactionExpectedList.add(test);
        transactionExpectedList.add(new Transaction(3002,1001,2001, new BigDecimal("200"), 1002, "Approved"));
        transactionExpectedList.add(new Transaction(3003,1001,2001, new BigDecimal("200"), 1002, "Approved"));
        transactionExpectedList.add(new Transaction(3004,1001,2001, new BigDecimal("200"), 1002, "Approved"));
        user.create("Tester1", "password");
        sut.createTransaction(new Transaction(3001,1001,2001, new BigDecimal("200"), 1002, "Approved"));
        sut.createTransaction(new Transaction(3001,1001,2001, new BigDecimal("200"), 1002, "Approved"));
        sut.createTransaction(new Transaction(3001,1001,2001, new BigDecimal("200"), 1002, "Approved"));
        sut.createTransaction(new Transaction(3001,1001,2001, new BigDecimal("200"), 1002, "Approved"));
        List<Transaction> actualList = sut.listTransactions("Tester1");
        for (int i = 0; i < transactionExpectedList.size(); i++) {
            assertTransactionsMatch(transactionExpectedList.get(i), actualList.get(i));
        }

    }

    @Test
    public void getPendingTransactions_normal_list() {
        List<Transaction> transactionExpectedList = new ArrayList<>();
        user.create("Tester1", "password");
        transactionExpectedList.add(new Transaction(3001,1001,2001, new BigDecimal("200"), 1002, "Pending"));
        transactionExpectedList.add(new Transaction(3002,1001,2001, new BigDecimal("200"), 1002, "Pending"));
        sut.createTransaction(new Transaction(3001,1001,2001, new BigDecimal("200"), 1002, "Pending"));
        sut.createTransaction(new Transaction(3001,1001,2001, new BigDecimal("200"), 1002, "Pending"));
        List<Transaction> actualList = sut.getPendingTransactions("Tester1");
        assertEquals(transactionExpectedList.size(), actualList.size());
        for (int i = 0; i < actualList.size(); i++) {
            assertTransactionsMatch(transactionExpectedList.get(i), actualList.get(i));
        }

    }
    @Test
    public void getPendingTransactions_none_list() {
        List<Transaction> transactionExpectedList = new ArrayList<>();
        user.create("Tester1", "password");
        sut.createTransaction(new Transaction(3001,1001,2001, new BigDecimal("200"), 1002, "Approved"));
        sut.createTransaction(new Transaction(3001,1001,2001, new BigDecimal("200"), 1002, "Approved"));

        assertEquals(transactionExpectedList.size(), sut.getPendingTransactions("Tester1").size());
    }

    @Test
    public void approveRequest_changes_to_approve() {
        user.create("Tester1", "password");
        user.create("Tester2", "password");
        Transaction testPend = new Transaction(3001,1001,2001, new BigDecimal("200"), 1002, "Pending");
        Transaction expectedApproval = new Transaction(3002, 1002, 2002, new BigDecimal("200.00"), 1002, "Approved");
        account.requestTEBucks(testPend);
        sut.approveRequest(sut.getTransaction(3002),"Tester2");
        assertTransactionsMatch(expectedApproval, sut.getTransaction(3002));


    }
    @Test
    public void denyRequest_should_delete_transaction() {
        user.create("Tester1", "password");
        user.create("Tester2", "password");
        Transaction testPend = new Transaction(3001,1001,2001, new BigDecimal("200"), 1002, "Pending");
        account.requestTEBucks(testPend);
        sut.denyRequest(3002,"Tester2");
        assertEquals(sut.listTransactions("Tester2").size(), 0);


    }
    private void assertTransactionsMatch(Transaction expected, Transaction actual) {
        Assert.assertEquals(expected.getTransaction_id(), actual.getTransaction_id());
        Assert.assertEquals(expected.getUser_id(), actual.getUser_id());
        Assert.assertEquals(expected.getTarget_id(), actual.getTarget_id());
        Assert.assertEquals(expected.getAccount_id(), actual.getAccount_id());
        Assert.assertEquals(expected.getAmount().setScale(2, RoundingMode.HALF_EVEN), actual.getAmount().setScale(2, RoundingMode.HALF_EVEN));
        Assert.assertEquals(expected.getStatus(), actual.getStatus());


    }

        }

