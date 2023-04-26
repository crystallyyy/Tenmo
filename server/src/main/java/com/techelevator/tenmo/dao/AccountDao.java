package com.techelevator.tenmo.dao;

import java.security.Principal;

public interface AccountDao {
    Account getAccount(Principal principal);
}
