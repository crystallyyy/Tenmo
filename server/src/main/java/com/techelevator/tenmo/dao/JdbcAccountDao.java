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

    @Override
    public List<Account> getAccounts(Principal principal){
        List<Account> accountList = new ArrayList<>();
        String sql = "SELECT * FROM account AS a " +
                " JOIN tenmo_user AS tu ON a.user_id = tu.user_id " +
                " WHERE username = ?;";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, principal.getName());
        while (results.next()) {
            accountList.add(mapRowToAccount(results));
        }
        return accountList;
    }

    public BigDecimal getAccountBalanceById(int userId){
        String sql1 = "SELECT balance FROM account WHERE user_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql1, userId);
        BigDecimal balance = result.getBigDecimal("balance");
        return balance;
    }


    public boolean createAccount(Account newAccount) {

        String sql = "INSERT INTO account (user_id, balance) VALUES (?, ?) RETURNING account_id;";
        try {
            int newAccId = jdbcTemplate.queryForObject(sql, Integer.class, newAccount.getUser_id(), newAccount.getBalance());

        } catch (CannotGetJdbcConnectionException e) {
            return false;

        } catch (BadSqlGrammarException e) {
            return false;
        } catch (DataIntegrityViolationException e) {
            return false;

        }
        return true;
    }


//    public Account transferTEBucks(Transaction transaction){
//
//        Account userAccount = null;
//        String sql0 = "UPDATE account SET balance = ? " +
//                "WHERE user_id = ?;";
//
//        BigDecimal currentBalance = getAccountBalanceById(transaction.getUser_id());
//
//        if(currentBalance.compareTo(transaction.getAmount()) != -1 && transaction.getUser_id() != transaction.getTarget_id()) {
//
//            try {
//                jdbcTemplate.update(sql0, currentBalance.subtract(transaction.getAmount()),transaction.getUser_id());
//                userAccount = getAccount(transaction.getUser_id());
//            } catch (CannotGetJdbcConnectionException e) {
//                throw new DaoException("Unable to connect ot server or db", e);
//            } catch (BadSqlGrammarException e) {
//                throw new DaoException("SQL syntax error", e);
//            } catch (DataIntegrityViolationException e) {
//                throw new DaoException("Data Integrity violation", e);
//            }
//        } else {
//            //TODO: create exception
//            throw new TenmoException("Insufficient funds or invalid user ID");
//        }
//
//        BigDecimal receiverBalance = getAccountBalanceById(transaction.getTarget_id());
//        try {
//            int numRows = jdbcTemplate.update(sql0, receiverBalance.add(transaction.getAmount()), transaction.getTarget_id());
//
//        } catch (CannotGetJdbcConnectionException e) {
//            throw new DaoException("Unable to connect ot server or db", e);
//        } catch (BadSqlGrammarException e) {
//            throw new DaoException("SQL syntax error", e);
//        } catch (DataIntegrityViolationException e) {
//            throw new DaoException("Data Integrity violation yo!", e);
//        }
//
//        String sql4 = "INSERT INTO transactions (user_id, account_id, amount, target_id, status) " +
//                "VALUES (?, ?, ?, ?, ?) RETURNING transaction_id;";
//
//        String sql = "SELECT account_id FROM account WHERE user_id = ?;";
//        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, transaction.getTarget_id());
//        int accountId2 = result.getInt("account_id");
//        int transactionId = jdbcTemplate.queryForObject(sql4, int.class, transaction.getUser_id(), transaction.getAccount_id(),
//                transaction.getAmount(), transaction.getTarget_id(), transaction.getStatus());
//        int transactionId2 = jdbcTemplate.queryForObject(sql4, int.class, transaction.getTarget_id(), accountId2, transaction.getAmount(),
//                transaction.getTarget_id(), transaction.getStatus());
//
//        return userAccount;
//    }


    @Override
    public void requestTEBucks(Transaction transaction){
        String sql1 = "INSERT INTO transactions (user_id, account_id, amount, target_id, status) " +
                    "VALUES (?, ?, ?, ?, ?) RETURNING transaction_id;";

        int transactionId1 = jdbcTemplate.queryForObject(sql1, int.class,  transaction.getUser_id(), transaction.getAccount_id(),
                transaction.getAmount(), transaction.getTarget_id(), "Pending");
        int transactionId2 = jdbcTemplate.queryForObject(sql1, int.class, transaction.getTarget_id(), transaction.getAccount_id(),
                transaction.getAmount(), transaction.getTarget_id(), "Pending");

        //separate method for this?, return string?
        String sql3 = "SELECT username FROM tenmo_user WHERE user_id = ?;";
        SqlRowSet result1 = jdbcTemplate.queryForRowSet(sql3, transaction.getUser_id());
        String requestingUsername = result1.getString("username");


        SqlRowSet result2 = jdbcTemplate.queryForRowSet(sql3, transaction.getTarget_id());
        String targetUsername = result2.getString("username");

        System.out.println("The user " + requestingUsername + " is requesting $" + transaction.getAmount() + " from user " + targetUsername );
    }


    private Account mapRowToAccount(SqlRowSet rowSet){
        Account account = new Account();
        account.setAccount_id(rowSet.getInt("account_id"));
        account.setUser_id(rowSet.getInt("user_id"));
        account.setBalance(rowSet.getBigDecimal("balance"));

        return account;
    }
}
