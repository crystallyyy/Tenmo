package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
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

    @Override
    public Account getAccount(Principal principal) {

        Account account = null;
        String sql = "SELECT * FROM account AS a " +
                " JOIN tenmo_user AS tu ON a.user_id = tu.user_id " +
                " WHERE username = ?;";
        try {
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, principal.getName());
        if (result.next()) {
            account = mapRowToAccount(result);
        }
    } catch (CannotGetJdbcConnectionException e) {
        throw new DaoException("Unable to connect to server or database", e);
    } catch (BadSqlGrammarException e) {
        throw new DaoException("SQL syntax error", e);
    }


        return account;
    }

    public Account getAccount(int id){
        Account account = null;
        String sql = "SELECT * FROM account " +
                "WHERE account_id = ?;";

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
        if (result.next()) {
            account = mapRowToAccount(result);
        }
        return account;
    }
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



//        catch (DataIntegrityViolationException e) {
//            System.out.println("poop");
//            return false;
//
//        }
public Transaction getTransaction(int transactionId) {
    Transaction transaction = null;
    String sql = "SELECT transaction_id, account_id, user_id, amount, target_id, status FROM transactions WHERE transaction_id = ?;";
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
    public Transaction transferTEBucks(Transaction transaction) {
        //TODO: change parameters
        Transaction newTransaction = null;
        String sql = "INSERT INTO transactions (account_id, user_id, amount, target_id, status) VALUES (?, ?, ?, ?, ?) RETURNING transaction_id;";
        try {
            System.out.println(transaction.getAccount_id());
            int newTransactionId = jdbcTemplate.queryForObject(sql, Integer.class, transaction.getAccount_id(), transaction.getUser_id(), transaction.getAmount(), transaction.getTarget_id(), transaction.getStatus());

            newTransaction = getTransaction(newTransactionId);

        } catch (CannotGetJdbcConnectionException e) {
            System.out.println("Errpr 1");

        } catch (BadSqlGrammarException e) {
            System.out.println("Error 2");

        }
            return newTransaction;
//        String sql1 = "UPDATE account SET user_id = ?, balance = ? " +
//                "WHERE account_id = ?;";
//        System.out.println(newTransaction.getTransaction_id());
//        System.out.println(getAccount(newTransaction.getAccount_id()).getUser_id());
//        System.out.println(getAccount(newTransaction.getAccount_id()).getBalance());
//        System.out.println(newTransaction.getAmount());
////        if(transaction.getAmount().compareTo(getAccount(transaction.getAccount_id()).getBalance()) == 1) {
//
//            try {
//                jdbcTemplate.update(sql1, getAccount(newTransaction.getUser_id()), getAccount(newTransaction.getUser_id()).getBalance().subtract(newTransaction.getAmount()),
//                        newTransaction.getAccount_id());
//            } catch (CannotGetJdbcConnectionException e) {
//                throw new DaoException("Unable to connect ot server or db", e);
//            } catch (BadSqlGrammarException e) {
//                throw new DaoException("SQL syntax error", e);
//            } catch (DataIntegrityViolationException e) {
//                throw new DaoException("Data Integrity violation", e);
//            }
////        } else {
////            //TODO: create exception
////            throw new RuntimeException("Insufficient funds or invalid user ID");
////        }
//        //will method continue running?
//
//        String sql2 = "UPDATE account SET balance = ? " +
//                "WHERE user_id = ?;";
//
//        try {
//            int numRows = jdbcTemplate.update(sql2, getAccount(newTransaction.getTarget_id()).getBalance().add(newTransaction.getAmount()), newTransaction.getTarget_id());
//
//        } catch (CannotGetJdbcConnectionException e) {
//            throw new DaoException("Unable to connect ot server or db", e);
//        } catch (BadSqlGrammarException e) {
//            throw new DaoException("SQL syntax error", e);
//        } catch (DataIntegrityViolationException e) {
//            throw new DaoException("Data Integrity violation yo!", e);
//        }
//
//        //different method for this insert?
//
////        String sql3 = "INSERT INTO transactions (account_id, amount, date_and_time, target_id, status) " +
////                "VALUES (?, ?, ?, ?) RETURNING transaction_id;";
////        int transactionId = jdbcTemplate.queryForObject(sql3, int.class, getAccount(userId).getAccount_id(),
////                amountToTransfer, LocalDate.now(), userId, "Approved");
//        return newTransaction;
    }

    @Override
    public void addBal(int user_id, int account_id, BigDecimal amount) {
        String sql = "UPDATE account SET balance = balance + ? " +
                "WHERE user_id = ? AND account_id = ?;";
        jdbcTemplate.update(sql, amount, user_id, account_id);
    }

    @Override
    public void decreaseBal(int target_id, int account_id, BigDecimal amount) {
        String sql = "UPDATE account SET balance =  balance - ? " +
                "WHERE user_id = ? AND account_id = ?;";
        jdbcTemplate.update(sql, amount, target_id, account_id);
    }

    @Override
    public void requestTEBucks(Account accountRequesting, BigDecimal amountRequested, int targetId){
        //TODO: to and from usernames need to be printed
        String sql = "INSERT INTO transactions (account_id, amount, date_and_time, target_id, status) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING transaction_id;";

        int transactionId = jdbcTemplate.queryForObject(sql, int.class, accountRequesting.getAccount_id(),
                amountRequested, LocalDate.now(), targetId, "Pending");

    }

    private Transaction mapRowToTransaction (SqlRowSet row) {
        Transaction transaction = new Transaction(
                row.getInt("transaction_id"),
                row.getInt("account_id"),
                row.getInt("user_id"),
                row.getBigDecimal("amount"),
                row.getInt("target_id"),
                row.getString("status")
        );
    }
    private Account mapRowToAccount(SqlRowSet rowSet){
        Account account = new Account();
        account.setAccount_id(rowSet.getInt("account_id"));
        account.setUser_id(rowSet.getInt("user_id"));
        account.setBalance(rowSet.getBigDecimal("balance"));

        return account;
    }
}
