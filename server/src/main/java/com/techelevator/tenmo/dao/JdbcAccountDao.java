package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.exception.TenmoException;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao{
    private JdbcTemplate jdbcTemplate;
    private Transaction transaction;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public Account getAccount(int id){
        Account account = null;
        String sql = "SELECT * FROM account " +
                "WHERE user_id = ?;";

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
        if (result.next()) {
            account = mapRowToAccount(result);
        }
        return account;
    }

    public Account getPrimaryAccount(int id){
        Account account = null;
        String sql = "SELECT * FROM account " +
                "WHERE user_id = ? AND is_primary = true;";

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
        if (result.next()) {
            account = mapRowToAccount(result);
        }
        return account;
    }

    @Override
    public List<Account> getAccounts(String username){
        List<Account> accountList = new ArrayList<>();
        String sql = "SELECT account_id, a.user_id, balance, is_primary FROM account AS a " +
                " JOIN tenmo_user AS tu ON a.user_id = tu.user_id " +
                " WHERE username = ?;";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
        while (results.next()) {
            accountList.add(mapRowToAccount(results));
        }
        return accountList;
    }

    public Account createAccount(Account newAccount) {
        String sql = "INSERT INTO account (user_id, balance, is_primary) VALUES (?, ?, ?) RETURNING account_id;";
        try {
            int newAccId = jdbcTemplate.queryForObject(sql, Integer.class, newAccount.getUser_id(), newAccount.getBalance(), newAccount.isIs_primary());

        } catch (CannotGetJdbcConnectionException e) {
            System.out.println("Jdbc connection error");
        } catch (BadSqlGrammarException e) {
            System.out.println("SQL grammar error");
        } catch (DataIntegrityViolationException e) {
            System.out.println("Data integrity error");
        }
        Account createdAccount = getAccount(newAccount.getUser_id());
        return createdAccount;
    }


    public void transferTEBucks(Transaction transaction) {
        //TODO: change parameters
        String sql = "INSERT INTO transactions (user_id, account_id, amount, target_id, status) VALUES (?, ?, ?, ?, ?) RETURNING transaction_id;";
        String sql2 = "INSERT INTO transactions(user_id, account_id, amount, target_id, status) VALUES (?, (SELECT account_id FROM account WHERE user_id = ? AND is_primary = true), ?, ?, ?) RETURNING transaction_id;";
        try {
            jdbcTemplate.queryForObject(sql, Integer.class, transaction.getUser_id(), transaction.getAccount_id(), transaction.getAmount(), transaction.getTarget_id(), transaction.getStatus());
            jdbcTemplate.queryForObject(sql2, Integer.class, transaction.getTarget_id(), getAccount(transaction.getTarget_id()).getAccount_id(), transaction.getAmount(), transaction.getUser_id(), transaction.getStatus());


            } catch (CannotGetJdbcConnectionException e) {
                throw new DaoException("Unable to connect ot server or db", e);
            } catch (BadSqlGrammarException e) {
                throw new DaoException("SQL syntax error", e);
            } catch (DataIntegrityViolationException e) {
                throw new DaoException("Data Integrity violation", e);
            }
    }


    @Override
    public int addBal(int target_id, int account_id, BigDecimal amount) {
        String sql = "UPDATE account SET balance = balance + ? " +
                "WHERE user_id = ? AND account_id = ? AND is_primary = true;";
        int numRows = jdbcTemplate.update(sql, amount, target_id, account_id);
        return numRows;
    }

    @Override
    public int decreaseBal(int user_id, int account_id, BigDecimal amount) {
        String sql = "UPDATE account SET balance =  balance - ? " +
                "WHERE user_id = ? AND account_id = ? AND is_primary = true;";
        int numRows = jdbcTemplate.update(sql, amount, user_id, account_id);
        return numRows;
    }


    @Override
    public void requestTEBucks(Transaction transaction){
        String requestingUsername;
        String targetUsername;
        String sql1 = "INSERT INTO transactions (user_id, account_id, amount, target_id, status) " +
                    "VALUES (?, ?, ?, ?, ?) RETURNING transaction_id;";

        int transactionId1 = jdbcTemplate.queryForObject(sql1, int.class,  transaction.getUser_id(), transaction.getAccount_id(),
                transaction.getAmount(), transaction.getTarget_id(), "Pending");
        int transactionId2 = jdbcTemplate.queryForObject(sql1, int.class, transaction.getTarget_id(), getAccount(transaction.getTarget_id()).getAccount_id(),
                transaction.getAmount(), transaction.getTarget_id(), "Pending");

        System.out.println(getAccount(transaction.getTarget_id()).getAccount_id());
        //separate method for this?, return string?
        String sql3 = "SELECT username FROM tenmo_user WHERE user_id = ?;";
        SqlRowSet result1 = jdbcTemplate.queryForRowSet(sql3, transaction.getUser_id());
        SqlRowSet result2 = jdbcTemplate.queryForRowSet(sql3, transaction.getTarget_id());

        if(result1.next()) {
            requestingUsername = result1.getString("username");
        }
        if(result2.next()){
            targetUsername = result2.getString("username");
        }
     //  System.out.println("The user " + requestingUsername + " is requesting $" + transaction.getAmount() + " from user " + targetUsername );
    }


    private Account mapRowToAccount(SqlRowSet rowSet){
        Account account = new Account();
        account.setAccount_id(rowSet.getInt("account_id"));
        account.setUser_id(rowSet.getInt("user_id"));
        account.setBalance(rowSet.getBigDecimal("balance"));
        account.setIs_primary(rowSet.getBoolean("is_primary"));
        return account;
    }
}
