package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

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
        String sql = "SELECT transaction_id, transactions.account_id, amount, date_and_time, target_id FROM transactions JOIN account ON account.account_id = transactions.account_id JOIN tenmo_user as t ON t.user_id = account.user_id WHERE username = ?";
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
        String sql = "SELECT transaction_id, account_id, amount, date_and_time, target_id FROM transactions WHERE transaction_id = ?;";
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

    private Transaction mapRowToTransaction (SqlRowSet row) {
        Transaction transaction = new Transaction(
                row.getInt("transaction_id"),
                row.getInt("account_id"),
                row.getBigDecimal("amount"),
                row.getDate("date_and_time").toLocalDate(),
                row.getInt("target_id")
        );
        return transaction;
    }
}
