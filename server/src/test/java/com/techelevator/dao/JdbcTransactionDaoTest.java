package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcTransactionDao;
import com.techelevator.tenmo.model.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class JdbcTransactionDaoTest extends BaseDaoTests {
    private JdbcTransactionDao sut;

    @Before
    public void setUp() throws Exception {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcTransactionDao(jdbcTemplate);
    }

    @Test
    public void listTransactions() {
//        List<Transaction> transactionExpectedList = new ArrayList<>();
//        Transaction transaction = new Transaction()
//        sut.listTransactions();
    }

    @Test
    public void getTransaction() {
    }
}