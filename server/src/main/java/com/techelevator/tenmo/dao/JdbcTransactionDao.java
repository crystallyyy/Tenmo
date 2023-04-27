package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransactionDao implements TransactionDao {
    private JdbcTemplate jdbcTemplate;
    public JdbcTransactionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Transaction> listTransactions(String username) {
        List<Transaction> transactionList = new ArrayList<>();
        String sql = "SELECT transaction_id, transactions.account_id, amount, date_and_time, target_id, status FROM transactions JOIN account ON account.account_id = transactions.account_id JOIN tenmo_user as t ON t.user_id = account.user_id WHERE username = ?";

        String sql = "SELECT transaction_id, transactions.account_id, amount, date_and_time, target_id, status FROM " +
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
        String sql = "SELECT transaction_id, account_id, amount, date_and_time, target_id, status FROM transactions WHERE transaction_id = ?;";
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

    public List<Transaction> getPendingTransactions(Principal principal){
        List<Transaction> pendingTransactions = new ArrayList<>();
        String sql = "SELECT transaction_id, transactions.account_id, amount, date_and_time, target_id, status FROM " +
                "transactions JOIN account ON account.account_id = transactions.account_id JOIN tenmo_user as t ON t.user_id = account.user_id WHERE username = ? AND status = 'Pending'";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, principal.getName());
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

    public Transaction approveRequest(Transaction transactionApproved, int transactionId){
        Transaction transactionUpdated = null;

        String sql1 = "UPDATE transactions SET status = ? " +
                "WHERE transaction_id = ?;";

        jdbcTemplate.update(sql1, "Approved", transactionId);

        String sql2 = "UPDATE account SET balance = ? " +
                "WHERE account_id = ?;";
        String sql3 = "SELECT balance FROM account WHERE account_id = ?;";

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql3, transactionApproved.getAccount_id());
        BigDecimal currentBalance = result.getBigDecimal("balance");

        jdbcTemplate.update(sql2,currentBalance.add(transactionApproved.getAmount()), transactionApproved.getAccount_id());

        String sql4 = "UPDATE account SET balance = ? " +
                "WHERE user_id = ?;";
        String sql5 = "SELECT balance FROM account WHERE user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql5, transactionApproved.getTarget_id());
        BigDecimal currentBalance2 = results.getBigDecimal("balance");

        jdbcTemplate.update(sql4, currentBalance2.subtract(transactionApproved.getAmount()), transactionApproved.getTarget_id());

        transactionUpdated = getTransaction(transactionId);

        //TODO: consider sout to conceal information in transaction
        return transactionUpdated;
    }

    public void denyRequest(int transactionId){
        String sql =  "DELETE FROM transactions WHERE transaction_id = ?;";
        jdbcTemplate.update(sql, transactionId);
    }

    private Transaction mapRowToTransaction (SqlRowSet row) {
        Transaction transaction = new Transaction(
                row.getInt("transaction_id"),
                row.getInt("account_id"),
                row.getBigDecimal("amount"),
                row.getDate("date_and_time").toLocalDate(),
                row.getInt("target_id"),
                row.getString("status")
        );
        return transaction;
    }
}
