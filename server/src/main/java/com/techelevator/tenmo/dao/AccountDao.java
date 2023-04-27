package com.techelevator.tenmo.dao;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;

public interface AccountDao {
    Account getAccount(Principal principal);
    Account transferTEBucks(int id, Account account, BigDecimal amountToTransfer);
    Account requestTEBucks(int userId, BigDecimal amount, LocalDate date, int targetUserId);
    public boolean createAccount(Account newAccount);
}
