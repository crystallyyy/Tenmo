package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.exception.TenmoException;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class JdbcTransactionDao implements TransactionDao {
    private JdbcTemplate jdbcTemplate;
    List<Transaction> transactions = new ArrayList<>();

    public JdbcTransactionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Transaction> listTransactions(String username) {
        List<Transaction> transactionList = new ArrayList<>();
        String sql = "SELECT transaction_id, transactions.account_id, transactions.user_id, amount, target_id, status FROM " +
                "transactions JOIN account ON account.account_id = transactions.account_id JOIN tenmo_user as t ON t.user_id = account.user_id WHERE username = ?";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
            while (results.next()){
                Transaction transaction = mapRowToTransaction(results);
                transactionList.add(transaction);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL syntax error", e);
        }
        return transactionList;
    }

    public Transaction getTransaction(int transactionId) {
        Transaction transaction = null;
        String sql = "SELECT transaction_id, user_id, account_id, amount, target_id, status FROM transactions WHERE transaction_id = ?;";
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, transactionId);
            if (result.next()) {
                transaction = mapRowToTransaction(result);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL syntax error", e);
        }
        return transaction;
    }


    public List<Transaction> getPendingTransactions(String name){
        List<Transaction> pendingTransactions = new ArrayList<>();
        int userId = 0;
        String sql = "SELECT transaction_id, transactions.user_id, transactions.account_id, amount, target_id, status FROM " +
                "transactions JOIN account ON account.account_id = transactions.account_id JOIN tenmo_user as t ON t.user_id = account.user_id WHERE transactions.user_id = ? AND status = 'Pending'";

        String sql1 = "SELECT user_id FROM tenmo_user WHERE username = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql1, name);
        if(result.next()){
            userId = result.getInt("user_id");
        }
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            while (results.next()){
                Transaction transaction = mapRowToTransaction(results);
               pendingTransactions.add(transaction);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("SQL syntax error", e);
        }

        return pendingTransactions;
    }



    public Transaction createTransaction(Transaction transaction){
        Transaction newTransaction = null;
        String sql = "INSERT INTO transactions (user_id, account_id, amount, target_id, status) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING transaction_id;";
        int transactionId = jdbcTemplate.queryForObject(sql, int.class, transaction.getUser_id(), transaction.getAccount_id(), transaction.getAmount(),
                transaction.getTarget_id(), transaction.getStatus());
        newTransaction = getTransaction(transactionId);
        return newTransaction;
    }

    public void approveRequest(Transaction transaction, String username){
        int userId =0;
        String sql = "SELECT user_id FROM tenmo_user WHERE username = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, username);
        if(result.next()) {
            userId = result.getInt("user_id");
        }
        System.out.println(userId);
        String sql1 = "UPDATE transactions SET status = ? " +
                "WHERE transaction_id = ? ;";
        String sql2 = "UPDATE transactions SET status = ? " +
                "WHERE transaction_id = ? - 1;";

        jdbcTemplate.update(sql1, "Approved", transaction.getTransaction_id());
        jdbcTemplate.update(sql2, "Approved", transaction.getTransaction_id());



    }

    public void denyRequest(int transactionId, String name){
        int userId = 0;
        String sql1 = "SELECT user_id FROM tenmo_user WHERE username = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql1, name);
        if(result.next()){
            userId = result.getInt("user_id");
        }
        String sql =  "DELETE FROM transactions WHERE transaction_id = ? AND target_id = ? AND status = 'Pending';";
        try {
            jdbcTemplate.update(sql, transactionId, userId);

        } catch(Exception e){
            throw new TenmoException("You are not authorized to deny this request.");
        }
        String sql2 = "DELETE FROM transactions WHERE transaction_id = ? - 1;";
         jdbcTemplate.update(sql2, transactionId);
    }


    private Transaction mapRowToTransaction (SqlRowSet row) {
        Transaction transaction = new Transaction(
                row.getInt("transaction_id"),
                row.getInt("user_id"),
                row.getInt("account_id"),
                row.getBigDecimal("amount"),
                row.getInt("target_id"),
                row.getString("status")
        );
        return transaction;
    }


    static private boolean emptyString(String s) {
        // Check if a string is null or empty
        return (s == null || s.trim().length() == 0);
    }

}
