package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

public interface AccountDao {
    Account getAccount(int user_id);
    List<Account> getAccounts(String username);

    public Account createAccount(Account newAccount);

    public void transferTEBucks(Transaction transaction);

    int addBal(int user_id, int account_id, BigDecimal amount);
    int decreaseBal(int target_id, int account_id, BigDecimal amount);

    Account getPrimaryAccount(int id);

    void requestTEBucks(Transaction transaction);
}
