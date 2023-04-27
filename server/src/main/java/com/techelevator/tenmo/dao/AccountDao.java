package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

public interface AccountDao {
    Account getAccount(Principal principal);
    List<Account> getAccounts(Principal principal);
    Account transferTEBucks(int id, Account account, BigDecimal amountToTransfer);

    public boolean createAccount(Account newAccount);

    void requestTEBucks(Account account, BigDecimal amount, int targetId);
}
