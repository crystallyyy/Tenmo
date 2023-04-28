package com.techelevator.tenmo.dao;

import com.techelevator.dao.BaseDaoTests;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class JdbcAccountDaoTest extends BaseDaoTests {

//    private static final Account ACCOUNT_1 = new Account(2001, 1001, new BigDecimal(1000.00));
//    private static final Account ACCOUNT_2 = new Account(2002, 1002, new BigDecimal(1250.00));

    //before push, tell philip copy test class
    private JdbcAccountDao dao;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup(){
        dao = new JdbcAccountDao(jdbcTemplate);
    }

    @Test
    public void getAccount_return_account(){

    }

}