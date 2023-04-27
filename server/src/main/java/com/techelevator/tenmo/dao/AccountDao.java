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
    Transaction requestTEBucks(int userId, BigDecimal amount, LocalDate date, int targetUserId);
    public boolean createAccount(Account newAccount);
}
