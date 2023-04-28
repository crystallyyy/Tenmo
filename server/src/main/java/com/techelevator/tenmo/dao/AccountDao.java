package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

public interface AccountDao {
    Account getAccount(int user_id);
    List<Account> getAccounts(Principal principal);

    public boolean createAccount(Account newAccount);

    public void transferTEBucks(Transaction transaction);

    void addBal(int user_id, int account_id, BigDecimal amount);
    void decreaseBal(int target_id, int account_id, BigDecimal amount);



    void requestTEBucks(Transaction transaction);
}
