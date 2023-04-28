package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

public interface AccountDao {
    List<Account> getAccounts(Principal principal);
    Account transferTEBucks(Transaction transaction);

    public boolean createAccount(Account newAccount);

    void requestTEBucks(Transaction transaction);
}
