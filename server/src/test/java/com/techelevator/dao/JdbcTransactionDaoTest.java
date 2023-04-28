package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcTransactionDao;
<<<<<<< HEAD
=======
import com.techelevator.tenmo.dao.JdbcUserDao;
>>>>>>> main
import com.techelevator.tenmo.model.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

<<<<<<< HEAD
import java.util.ArrayList;
=======
>>>>>>> main
import java.util.List;

import static org.junit.Assert.*;

public class JdbcTransactionDaoTest extends BaseDaoTests {
    private JdbcTransactionDao sut;

    private JdbcTransactionDao sut;

    @Before
    public void setUp() throws Exception {
<<<<<<< HEAD
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcTransactionDao(jdbcTemplate);
=======
  //      sut = new JdbcTransactionDao(dataSource);
>>>>>>> main
    }

    @Test
    public void listTransactions() {
<<<<<<< HEAD
//        List<Transaction> transactionExpectedList = new ArrayList<>();
//        Transaction transaction = new Transaction()
//        sut.listTransactions();
=======
        //List<Transaction> transactions = sut.listTransactions()
        //assertEquals(size)
        //assertTransactionsMatch
>>>>>>> main
    }

    @Test
    public void getTransaction() {
    }
}