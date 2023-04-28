package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcTransactionDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.Assert.*;

public class JdbcTransactionDaoTest extends BaseDaoTests {

    private JdbcTransactionDao sut;

    @Before
    public void setUp() throws Exception {
  //      sut = new JdbcTransactionDao(dataSource);
    }

    @Test
    public void listTransactions() {
        //List<Transaction> transactions = sut.listTransactions()
        //assertEquals(size)
        //assertTransactionsMatch
    }

    @Test
    public void getTransaction() {
    }
}