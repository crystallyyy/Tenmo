package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;
import java.security.Principal;

public class JdbcAccountDao implements AccountDao{
    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account getAccount(int userId) {
        //TODO: use principal as parameter?
        Account account = null;
        String sql = "SELECT * FROM account WHERE user_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userId);

        if (result.next()){
            account = mapRowToAccount(result);
        } else {
            System.out.println("Error: user ID not found");
        }
        return account;
    }

    public Account transferTEBucks(int userId, Account currentAccount, BigDecimal amountToTransfer){
        Account userAccount = null;
        Account receiverAccount = null;
        String sql1 = "UPDATE account SET user_id = ?, balance = ? " +
                "WHERE account_id = ?;";
        //TODO: check balance has sufficient amount for transfer
        try {
            jdbcTemplate.update(sql1, currentAccount.getUser_id(), currentAccount.getBalance().subtract(amountToTransfer),
                    currentAccount.getAccount_id());
            userAccount = getAccount(currentAccount.getUser_id());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect ot server or db", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL syntax error", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data Integrity violation", e);
        }

        String sql2 = "UPDATE account SET balance = ? " +
                "WHERE user_id = ?;";

        try {
            //TODO: check 1 row affected, set as int
            jdbcTemplate.update(sql2, getAccount(userId).getBalance().add(amountToTransfer), userId);
            receiverAccount = getAccount(userId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect ot server or db", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL syntax error", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data Integrity violation yo!", e);
        }

        //TODO: insert row to transaction table

        return userAccount;
    }

    private Account mapRowToAccount(SqlRowSet rowSet){
        Account account = new Account();
        account.setAccount_id(rowSet.getInt("account_id"));
        account.setUser_id(rowSet.getInt("user_id"));
        account.setBalance(rowSet.getBigDecimal("balance"));

        return account;
    }
}
