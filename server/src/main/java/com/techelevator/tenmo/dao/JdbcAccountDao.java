package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;

@Component
public class JdbcAccountDao implements AccountDao{
    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account getAccount(Principal principal) {

        Account account = null;
        String sql = "SELECT * FROM account AS a " +
                " JOIN tenmo_user AS tu ON a.user_id = tu.user_id " +
                " WHERE username = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, principal.getName());

            account = mapRowToAccount(result);

        return account;
    }

    //create getAccount with id as parameter?
    public Account getAccount(int id){
        Account account = null;
        String sql = "SELECT * FROM account " +
                "WHERE user_id = ?;";

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
        account = mapRowToAccount(result);

        return account;
    }


    public Account transferTEBucks(int userId, Account currentAccount, BigDecimal amountToTransfer){
        //two account abjects as parameters
        Account userAccount = null;
        Account receiverAccount = null;
        String sql1 = "UPDATE account SET user_id = ?, balance = ? " +
                "WHERE account_id = ?;";

        if(amountToTransfer.compareTo(currentAccount.getBalance()) == 1 && userId != currentAccount.getUser_id()) {

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
        } else {
            System.out.println("Insufficient funds or invalid user ID");
        }
        //will method continue running?

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

        //different method for this insert?

        String sql3 = "INSERT INTO transactions (account_id, amount, date_and_time, target_id) " + //need status
                "VALUES (?, ?, ?, ?) RETURNING transaction_id;";
        int transactionId = jdbcTemplate.queryForObject(sql3, int.class, getAccount(userId).getAccount_id(),
                amountToTransfer, LocalDate.now(), userId);

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
