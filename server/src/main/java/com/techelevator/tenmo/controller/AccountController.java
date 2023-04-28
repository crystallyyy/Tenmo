package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.Account;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransactionDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class AccountController {
    private UserDao userDao;
    private AccountDao accountDao;
    private TransactionDao transactionDao;
    private Principal principal;


    public AccountController(UserDao userDao, AccountDao accountDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public List<Account> getBalance(Principal principal) {
        return accountDao.getAccounts(principal);

    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path = "/transfer", method = RequestMethod.PUT)
    public Account transferBucks(@Valid @RequestBody Transaction transaction){
        Account updatedAccount = accountDao.transferTEBucks(transaction);
        if(updatedAccount == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return updatedAccount;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/request", method = RequestMethod.POST)

    public void requestTransfer(@Valid @RequestBody Transaction transaction){
        accountDao.requestTEBucks(transaction);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public boolean create(@Valid @RequestBody Account account) {
        return accountDao.createAccount(account);
    }
}
