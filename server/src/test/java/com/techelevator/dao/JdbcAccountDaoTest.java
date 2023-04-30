package com.techelevator.dao;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.techelevator.dao.BaseDaoTests;
import com.techelevator.tenmo.dao.Account;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class JdbcAccountDaoTest extends BaseDaoTests {

    private static final User USER_1 = new User(1001, "user1", "password", "");
    private static final User USER_2 = new User(1002, "user2", "password", "");
    private static final User USER_3 = new User(1003, "user3", "password", "");

    private static final Account ACCOUNT_1 = new Account(2001, 1001, new BigDecimal(1000.00), true);
    private static final Account ACCOUNT_2 = new Account(2002, 1002, new BigDecimal(1250.00), true);
    private static final Account ACCOUNT_3 = new Account(2003, 1002, new BigDecimal(1000.00), false);
    private static final Account ACCOUNT_4 = new Account(2004, 1003, new BigDecimal(100.00), true);
    private static final Account ACCOUNT_5 = new Account(2005, 1003, new BigDecimal(1100.00), false);
    private Account testAccount;

    private JdbcAccountDao dao;
    private JdbcUserDao user;
    private JdbcTemplate jdbcTemplate;
    private Account accountTest;

    //TODO: We need to connect to a database / figure out Null Pointer Exceptions
    @Before
    public void setup(){
        dao = new JdbcAccountDao(jdbcTemplate);
        user = new JdbcUserDao(jdbcTemplate);
        testAccount = new Account(0, 10, new BigDecimal(120.00), true);
        user.create("Tester1", "test");

    }

    @Test
    public void getAccount_return_account(){
        assertAccountsMatch(ACCOUNT_1, dao.getAccount(1001));

//        account = dao.getAccount(1002);
//        assertAccountsMatch(ACCOUNT_2, account);
    }
    @Test
    public void getAccount_returns_null_when_id_not_found(){
        //what do we want to return when id not found?
        Account account = dao.getAccount(3);
        Assert.assertNull(account);
    }

    @Test
    public void getPrimaryAccount_returns_acc(){
        Account account = dao.getPrimaryAccount(1002);
        assertAccountsMatch(ACCOUNT_2, account);

        account = dao.getPrimaryAccount(1003);
        assertAccountsMatch(ACCOUNT_4, account);
    }

    @Test
    public void getAccounts_returns_correct_list(){
        List<Account> accounts = dao.getAccounts(USER_2.getUsername());
        Assert.assertEquals(2, accounts.size());

        assertAccountsMatch(ACCOUNT_2, accounts.get(0));
        assertAccountsMatch(ACCOUNT_3, accounts.get(1));
    }

    @Test
    public void getAccounts_returns_empty_list_when_user_not_found(){
        List<Account> accounts = dao.getAccounts("user1000");
        Assert.assertEquals(0, accounts.size());
    }

    @Test
    public void createAccount_returns_account_and_expected_values(){
        //had to change createAccount to return Account in order to test (was previously boolean return)
        Account createdAccount = dao.createAccount(testAccount, "Tester");
        int newId = createdAccount.getAccount_id();

        Assert.assertTrue(newId > 0);

        Account retrievedAcc = dao.getAccount(10);
        assertAccountsMatch(createdAccount, retrievedAcc);
    }

    @Test
    public void addBal_returns_1_row_updated(){
        //changed return from void to int
        int numRows = dao.addBal(1001, 2001, new BigDecimal(8.00));
        Assert.assertEquals(1, numRows);
    }

    @Test
    public void decreaseBal_returns_1_row_updated(){
        //changed return from void to int
        int numRows = dao.decreaseBal(1003, 2004, new BigDecimal(8.00));
        Assert.assertEquals(1, numRows);
    }

    private void assertAccountsMatch(Account expected, Account actual){
        Assert.assertEquals(expected.getAccount_id(), actual.getAccount_id());
        Assert.assertEquals(expected.getUser_id(), actual.getUser_id());
        Assert.assertEquals(expected.getBalance(), actual.getBalance());
        Assert.assertEquals(expected.isIs_primary(), actual.isIs_primary());
    }

}