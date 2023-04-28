package com.techelevator.tenmo.dao;

import com.techelevator.dao.BaseDaoTests;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JdbcTransactionDaoTest extends BaseDaoTests {
    private JdbcUserDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcUserDao(jdbcTemplate);
    }

    @Test
    public void listTransactions_returns_all_transactions(){

    }

}