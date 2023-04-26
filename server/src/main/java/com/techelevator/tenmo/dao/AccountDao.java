package com.techelevator.tenmo.dao;

import java.math.BigDecimal;
import java.security.Principal;

public interface AccountDao {
    Account getAccount(Principal principal);
    Account transferTEBucks(int id, Account account, BigDecimal amountToTransfer);
}
